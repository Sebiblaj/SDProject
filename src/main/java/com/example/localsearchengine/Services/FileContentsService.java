package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.ContentDTOS.ContentsDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileContentDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileSearchResult;
import com.example.localsearchengine.DTOs.FileSearchCriteria;
import com.example.localsearchengine.DTOs.LoggerMessage;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileContents;
import com.example.localsearchengine.KeywordSearch.FileContentsSpecification;
import com.example.localsearchengine.Persistence.FileContentsRepository;
import com.example.localsearchengine.Persistence.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FileContentsService {

    private final Set<FileSearchCriteria> cachedCriteria = ConcurrentHashMap.newKeySet();

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private FileContentsRepository fileContentsRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private CacheManager cacheManager;

    @Value("${kafka.topic.logs}")
    private String TOPIC;

    @Autowired
    public void KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Object message) {
        kafkaTemplate.send(TOPIC, message);
    }

    @Cacheable(value = "fileContents", key = "{#path, #filename, #extension}")
    public String getFileContents(String path, String filename,String extension) {

        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path, filename,extension);

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has accessed the contents for the file."
                )
        ));
        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        MetadataEntries metadataEntries = new MetadataEntries("lastaccess", String.valueOf(timestamp));
        metadataService.modifyMetadataForFile(path,filename,extension,new ArrayList<>(List.of(metadataEntries)));


        return fileContents != null ? fileContents.getContents() : null;
    }

    @Cacheable(value = "fileContentsPreview", key = "{#path, #filename, #extension}")
    public String getPreview(String path, String filename,String extension) {

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has accessed the contents preview for the file."
                )
        ));

        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        MetadataEntries metadataEntries = new MetadataEntries("lastaccess", String.valueOf(timestamp));
        metadataService.modifyMetadataForFile(path,filename,extension,new ArrayList<>(List.of(metadataEntries)));

        return fileContentsRepository.getFileContentsByPathAndFilename(path, filename,extension).getPreview();
    }

    @Cacheable(value = "fileContentsKeywords", key = "#criteria")
    public List<FileSearchResult> search(FileSearchCriteria criteria) {
        cachedCriteria.add(criteria);
        List<FileContents> results = fileContentsRepository.findAll(FileContentsSpecification.withCriteria(criteria));

        if (results.isEmpty()) {
            return null;
        }

        return results.stream().map(fileContent -> {
            List<Integer> lineNumbers = new ArrayList<>();
            List<String> excerpts = new ArrayList<>();

            List<String> lines = fileContent.getContents().lines().toList();
            for (int i = 0; i < lines.size(); i++) {
                for (String keyword : criteria.getKeywords()) {
                    if (lines.get(i).toLowerCase().contains(keyword.toLowerCase())) {
                        lineNumbers.add(i + 1);
                        excerpts.add(lines.get(i));
                    }
                }
            }

            File file = fileContent.getFile();
            String filename = file.getFilename();
            String path = file.getPath();
            String extension = file.getType().getType();

            long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            metadataService.modifyMetadataForFile(path, filename, extension,
                    List.of(new MetadataEntries("lastaccess", String.valueOf(timestamp))));

            return new FileSearchResult(filename, path, lineNumbers, excerpts);
        }).toList();
    }

    @Transactional
    public String setFileContents(List<FileContentDTO> fileFullContents) {
        List<String> paths = fileFullContents.stream()
                .map(FileContentDTO::getPath)
                .collect(Collectors.toList());

        List<String> filenames = fileFullContents.stream()
                .map(FileContentDTO::getFilename)
                .collect(Collectors.toList());

        List<String> extensions = fileFullContents.stream()
                .map(FileContentDTO::getExtension)
                .collect(Collectors.toList());

        List<File> files = fileRepository.findFilesByPathAndFilenameAndExtension(paths, filenames, extensions);

        Map<String, File> fileMap = files.stream().collect(Collectors.toMap(
                file -> file.getPath() + file.getFilename() + file.getType().getType(),
                file -> file
        ));

        List<FileContents> fileContentsList = new ArrayList<>();

        for (FileContentDTO fileDTO : fileFullContents) {
            String key = fileDTO.getPath() + fileDTO.getFilename() + fileDTO.getExtension();

            evictCache("fileContents",fileDTO.getPath(),fileDTO.getFilename(),fileDTO.getExtension());
            evictCache("fileContentsPreview",fileDTO.getPath(),fileDTO.getFilename(),fileDTO.getExtension());
            evictByNameAndPath(List.of(fileDTO.getFilename()), List.of(fileDTO.getPath()));

            File file = fileMap.get(key);
            if (file != null) {
                FileContents fileContents = fileContentsRepository.findByFile(file);
                String cleanContent = fileDTO.getContent().replace("\u0000", "");

                if (fileContents != null) {
                    fileContents.setContents(cleanContent);
                } else {
                    fileContents = new FileContents();
                    fileContents.setFile(file);
                    fileContents.setContents(cleanContent);
                }

                String preview = cleanContent.length() > 100 ? cleanContent.substring(0, 100) + "..." : cleanContent;
                fileContents.setPreview(preview);
                fileContentsList.add(fileContents);

                long timestamp = LocalDateTime.now()
                        .atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli();

                MetadataEntries lastAccess = new MetadataEntries("lastaccess", String.valueOf(timestamp));
                MetadataEntries lastModified = new MetadataEntries("lastmodified", String.valueOf(timestamp));

                metadataService.modifyMetadataForFile(
                        fileDTO.getPath(),
                        fileDTO.getFilename(),
                        fileDTO.getExtension(),
                        List.of(lastAccess, lastModified)
                );
            }
        }

        fileContentsRepository.saveAll(fileContentsList);

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        fileContentsList.size(),
                        fileContentsList.stream()
                                .map(f -> f.getFile().getPath() + "/" + f.getFile().getFilename())
                                .toList(),
                        "User has set the contents for the files."
                )
        ));

        return "Contents Added Successfully";
    }

    @Transactional
    public String setFileContents(String path, String filename,String extension, ContentsDTO contentsDTO) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path, filename,extension);
        String description;
        boolean success = true;
        if (fileContents != null) {
            fileContents.setContents(contentsDTO.getContent());
            fileContentsRepository.save(fileContents);
        } else {
            File file = fileRepository.getFileByPathAndFilenameAndExtension(path, filename,extension);
            if (file != null) {
                FileContents newFileContents = new FileContents();
                newFileContents.setFile(file);
                newFileContents.setContents(contentsDTO.getContent());
                fileContentsRepository.save(newFileContents);
            }else{
                success = false;
            }
        }

        evictCache("fileContents",path,filename,extension);
        evictCache("fileContentsPreview",path,filename,extension);
        evictByNameAndPath(List.of(filename), List.of(path));

        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        MetadataEntries metadataEntries = new MetadataEntries("lastaccess", String.valueOf(timestamp));
        List<MetadataEntries> entries = new ArrayList<>();
        entries.add(metadataEntries);
        if (success) {
            description = "User has set contents to the file.";
        }else{
            description = "User has tried to set contents to the file but the file does not exist.";
            MetadataEntries metadataEntries2 = new MetadataEntries("lastmodified",String.valueOf(timestamp));
            entries.add(metadataEntries2);
        }
        metadataService.modifyMetadataForFile(path,filename,extension,entries);

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        0,
                        new ArrayList<>(),
                        description
                )
        ));
        return success?"Success":"Failed";
    }

    @Transactional
    public void deleteFileContents(String path, String filename,String extension) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path, filename,extension);
        if (fileContents != null) {
            fileContentsRepository.delete(fileContents);
        }

        evictCache("fileContents",path,filename,extension);
        evictCache("fileContentsPreview",path,filename,extension);
        evictByNameAndPath(List.of(filename), List.of(path));

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has deleted the contents of the file."
                )
        ));

        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        MetadataEntries metadataEntries = new MetadataEntries("lastaccess", String.valueOf(timestamp));
        MetadataEntries metadataEntries2 = new MetadataEntries("lastmodified",String.valueOf(timestamp));
        metadataService.modifyMetadataForFile(path,filename,extension,new ArrayList<>(List.of(metadataEntries, metadataEntries2)));
    }

    private void evictCache(String value,String path,String filename,String extension){
        Cache cacheContents = cacheManager.getCache(value);
        if (cacheContents != null) {
            cacheContents.evict(List.of(
                    path,
                    filename,
                    extension
            ));
        }
    }

    public void evictByNameAndPath(List<String> names, List<String> paths) {
        Cache cache = cacheManager.getCache("fileContentsKeywords");
        if (cache == null) return;

        for (FileSearchCriteria criteria : cachedCriteria) {
            if (criteria.getNames().equals(names) && criteria.getPaths().equals(paths)) {
                cache.evict(criteria);
            }
        }
        cachedCriteria.removeIf(c -> c.getNames().equals(names) && c.getPaths().equals(paths));
    }
}
