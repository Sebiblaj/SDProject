package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.FileDTOS.*;
import com.example.localsearchengine.DTOs.LoggerMessage;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.Entities.FileEntity;
import com.example.localsearchengine.Entities.FileTag;
import com.example.localsearchengine.Entities.FileType;
import com.example.localsearchengine.Persistence.FileRepository;
import com.example.localsearchengine.Persistence.FileTagsRepository;
import com.example.localsearchengine.Persistence.FileTypeRepository;
import com.example.localsearchengine.ServiceExecutors.Convertors.FileConvertor;
import com.example.localsearchengine.ServiceExecutors.Convertors.FileDTOConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.example.localsearchengine.DTOs.LoggerMessage.ActivityDetails;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileTagsRepository fileTagsRepository;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileConvertor fileConvertor;

    @Autowired
    private FileDTOConverter fileDTOConverter;

    @Value("${kafka.topic.logs}")
    private String TOPIC;

    @Autowired
    public void KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Object message) {
        kafkaTemplate.send(TOPIC, message);
    }

    @Cacheable("files")
    public List<ReturnedFileDTO> getFiles() {

        List<ReturnedFileDTO> returnedFileList = new ArrayList<>();
        fileRepository.findAllFiles().forEach(file -> {
            returnedFileList.add(fileConvertor.convert(file));
        });

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        returnedFileList.size(),
                        returnedFileList.stream()
                                .map(f -> f.getPath() + "/" + f.getFilename())
                                .collect(Collectors.toList()),
                        "User has accessed all the files from the system."
                )
        ));

       return returnedFileList;
    }

    @Cacheable(value = "file", key = "{#filePath, #fileName, #extension}")
    public ReturnedFileDTO getFile(String fileName,String filePath,String extension){
        FileEntity fileEntity = fileRepository.getFileByPathAndFilenameAndExtension(filePath, fileName,extension);

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        fileEntity != null ? 1 : 0,
                        fileEntity != null ? new ArrayList<>(List.of(fileEntity.getPath() + "/" + fileEntity.getFilename())) : new ArrayList<>(),
                        "User has accessed the fileEntity."
                )
        ));
        return fileConvertor.convert(fileEntity);
    }

    @Transactional
    public String addFile(Object payload) {

        Objects.requireNonNull(cacheManager.getCache("files")).clear();

        if (payload instanceof List<?>) {
            boolean success=true;

            List<FileDTO> fileDTOs = objectMapper.convertValue(payload, new TypeReference<>() {});

            List<String> filenames = fileDTOs.stream().map(FileDTO::getFilename).toList();
            List<String> paths = fileDTOs.stream().map(FileDTO::getPath).toList();

            List<FileEntity> existingFileEntities = fileRepository.findFilesByPathAndFilename(paths, filenames);

            Set<String> existingFileKeys = existingFileEntities.stream()
                    .map(file -> file.getPath() + "|" + file.getFilename() + "|" + file.getType().getType())
                    .collect(Collectors.toSet());

            Map<String, FileType> fileTypeMap = fileTypeRepository.findAll().stream()
                    .collect(Collectors.toMap(FileType::getType, fileType -> fileType));

            List<FileEntity> newFileEntities = new ArrayList<>();

            for (FileDTO fileDTO : fileDTOs) {
                String key = fileDTO.getPath() + "|" + fileDTO.getFilename() + "|" + fileDTO.getType();
                if (existingFileKeys.contains(key)) {
                    continue;
                }

                Cache cache1 = cacheManager.getCache("getFileEntity");
                if(cache1 != null && cache1.get(fileDTO.getFilename()) != null){
                    cache1.evict(fileDTO.getFilename());
                }

                Cache cache2 = cacheManager.getCache("ext");
                if(cache2 != null && cache2.get(fileDTO.getType()) != null){
                    cache2.evict(fileDTO.getType());
                }

                FileEntity newFileEntity = fileDTOConverter.convert(fileDTO);

                FileType fileType = fileTypeMap.get(fileDTO.getType());
                if (fileType == null) {
                    success = false;
                    break;
                }
                newFileEntity.setType(fileType);
                newFileEntities.add(newFileEntity);
            }

            if (newFileEntities.isEmpty()) {
                success = false;
            }else{

                fileRepository.saveAll(newFileEntities);
            }

            String description;
            List<String> filesToAdd = new ArrayList<>();
            if (success) {
                description = "User has set multiple files into the system.";
                filesToAdd = newFileEntities.stream().map(f ->  f.getPath() + "/"+f.getFilename()).toList();
            }else{
                description = "User has tried to set multiple files into the system but failed";
            }

            sendMessage(new LoggerMessage(
                    Timestamp.valueOf(LocalDateTime.now()),
                    "sebir",
                    new ActivityDetails(
                            filesToAdd.size(),
                            filesToAdd,
                            description
                    )
            ));
            return "Files added successfully";
        }else if (payload instanceof Map) {
            FileDTO fileDTO = objectMapper.convertValue(payload, FileDTO.class);
            FileEntity newFileEntity = fileDTOConverter.convert(fileDTO);

            Cache cache1 = cacheManager.getCache("getFileEntity");
            if(cache1 != null && cache1.get(fileDTO.getFilename()) != null){
                cache1.evict(fileDTO.getFilename());
            }

            Cache cache2 = cacheManager.getCache("ext");
            if(cache2 != null && cache2.get(fileDTO.getType()) != null){
                cache2.evict(fileDTO.getType());
            }

            fileRepository.save(newFileEntity);

            sendMessage(new LoggerMessage(
                    Timestamp.valueOf(LocalDateTime.now()),
                    "sebir",
                    new ActivityDetails(
                            1,
                            new ArrayList<>(List.of(newFileEntity.getPath() + "/" + newFileEntity.getFilename())),
                            "User has set a file into the system."
                    )
            ));

            return "FileEntity added successfully";

        }
        return null;
    }

    @Transactional
    public String updateFile(String path,String filename,String extension, List<MetadataEntries> request) {
        FileEntity oldFileEntity = fileRepository.getFileByPathAndFilenameAndExtension(path, filename,extension);
        boolean success = true;
        if (oldFileEntity == null) {
            success = false;
        }else {

            for (MetadataEntries entry : request) {
                String fieldName = entry.getKey();
                String newValue = entry.getValue();

                try {
                    Field field = FileEntity.class.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    Object convertedValue = convertValue(field.getType(), newValue);

                    field.set(oldFileEntity, convertedValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    success = false;
                }
            }
        }

        evictCache(path,filename,extension);
        Objects.requireNonNull(cacheManager.getCache("files")).clear();
        Cache cache1 = cacheManager.getCache("getFileEntity");
        if(cache1 != null && cache1.get(filename) != null){
            cache1.evict(filename);
        }

        Cache cache2 = cacheManager.getCache("ext");
        if(cache2 != null && cache2.get(extension) != null){
            cache2.evict(extension);
        }

        String description;
        String filePath;
        String fileName;
        if(success){
            description = "User has updated all the files from the system.";
            filePath = oldFileEntity.getPath();
            fileName = oldFileEntity.getFilename();
            fileRepository.save(oldFileEntity);
        }else{
            description = "User has tried to update the file " + filename + " but file does not exist.";
            filePath = path;
            fileName = filename;
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        1,
                        new ArrayList<>(List.of(filePath+"/"+fileName)),
                        description
        )));

        return success ? "FileEntity updated successfully" : "FileEntity not found";
    }

    @Cacheable(value = "getFile",key = "#filename")
    public List<ReturnedFileDTO> searchFilesByName(String filename) {

        List<FileEntity> fileEntities = fileRepository.findAllByFilename(filename);
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if ( fileEntities == null) {
            return returnedFiles; }
        fileEntities.forEach(file -> returnedFiles.add(fileConvertor.convert(file)));

        System.out.println("returned fileEntities: " + fileEntities);
        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        returnedFiles.size(),
                        returnedFiles.stream()
                                .map(f-> f.getPath() + "/"+f.getFilename())
                                .collect(Collectors.toList()),
                        "User has accessed all the fileEntities with the same name in the system."
                )
        ));

        return returnedFiles;
    }

    @Cacheable(value = "ext",key = "#ext")
    public List<ReturnedFileDTO> searchFilesByExtension(String ext) {
        List<FileEntity> fileEntities = fileRepository.findAllByExtension(ext);
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if (fileEntities.isEmpty()) { return returnedFiles; }
        fileEntities.forEach(file -> {
            returnedFiles.add(fileConvertor.convert(file));
        });

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        returnedFiles.size(),
                        returnedFiles.stream()
                                .map(f-> f.getPath() + "/"+f.getFilename())
                                .collect(Collectors.toList()),
                        "User has accessed all the fileEntities with the same extension in the system."
                )
        ));
        return returnedFiles;
    }

    @Transactional
    public boolean deleteByPathAndFilename(List<PathAndName> files) {
        int deletedCount = fileRepository.deleteByPathAndFilename(files);

        for(PathAndName pathAndName : files){
            evictCache(pathAndName.getPath(),pathAndName.getName(),pathAndName.getExtension());
            Objects.requireNonNull(cacheManager.getCache("files")).clear();
            Cache cache1 = cacheManager.getCache("getFileEntity");
            if(cache1 != null && cache1.get(pathAndName.getName()) != null){
                cache1.evict(pathAndName.getName());
            }

            Cache cache2 = cacheManager.getCache("ext");
            if(cache2 != null && cache2.get(pathAndName.getExtension()) != null){
                cache2.evict(pathAndName.getExtension());
            }
        }

        if (deletedCount != files.size()) {

            sendMessage(new LoggerMessage(
                    Timestamp.valueOf(LocalDateTime.now()),
                    "sebir",
                    new ActivityDetails(
                            files.size(),
                            files.stream()
                                    .map(f-> f.getPath() +"/"+f.getName())
                                    .collect(Collectors.toList()),
                            "User has tried to delete the files following files: "+ files +"from the system and failed."
                    )
            ));

            throw new RuntimeException("Not all files were deleted. Expected: " + files.size() + ", but only deleted: " + deletedCount);
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        files.size(),
                        files.stream()
                                .map(f-> f.getPath() +"/"+f.getName())
                                .collect(Collectors.toList()),
                        "User has deleted the files successfully."
                )
        ));

        return true;
    }

    @Transactional
    public void deleteByPathAndFilename() {
        List<FileEntity> fileEntities = fileRepository.findAll();
        int size = fileEntities.size();
        List<String> names = fileEntities.stream().map(FileEntity::getFilename).toList();
        List<String> paths = fileEntities.stream().map(FileEntity::getPath).toList();
        List<String> extensions = fileEntities.stream().map(FileEntity::getType).map(FileType::getType).toList();

        for(int i = 0 ; i< names.size() ; i++){
            deleteAllTagsForFile(paths.get(i), names.get(i),extensions.get(i));
            evictCache(paths.get(i), names.get(i),extensions.get(i));
            Objects.requireNonNull(cacheManager.getCache("fileEntities")).clear();
            Cache cache1 = cacheManager.getCache("getFileEntity");
            if(cache1 != null && cache1.get(names.get(i)) != null){
                cache1.evict(names.get(i));
            }

            Cache cache2 = cacheManager.getCache("ext");
            if(cache2 != null && cache2.get(extensions.get(i)) != null){
                cache2.evict(extensions.get(i));
            }

        }
        fileRepository.deleteAll();

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        size,
                        names,
                        "User has deleted all the fileEntities in the system."
                )
        ));
    }

    public List<ReturnedFileDTO> findBySizeInterval(String min, String max) {
        List<FileEntity> fileEntities = fileRepository.findByFileSizeBetween(Long.parseLong(min), Long.parseLong(max));
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if (fileEntities.isEmpty()) { return returnedFiles; }
        fileEntities.forEach(file -> {
            returnedFiles.add(fileConvertor.convert(file));
        });

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        returnedFiles.size(),
                        returnedFiles.stream()
                                .map(f -> f.getPath() + "/"+f.getFilename())
                                .collect(Collectors.toList()),
                        "User has accessed the fileEntities within interval [" + min + ", " + max + "] from the system."
                )
        ));

        return returnedFiles;
    }

    private Object convertValue(Class<?> fieldType, String value) {
        if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == Timestamp.class) {
            return Timestamp.valueOf(value);
        } else {
            return value;
        }
    }


    /*
    The following methods are for the tags
     */

    @Cacheable(value = "tags",key = "#path+'/'+#filename")
    public List<Tag> getTagsForFile(String path, String filename) {
        List<FileTag> fileTags = fileRepository.findTagsForFile(path, filename);
        List<Tag> tags = new ArrayList<>();
        if (!fileTags.isEmpty()) {
            fileTags.forEach(fileTag -> tags.add(new Tag(fileTag.getTag())));
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has accessed the file to retrieve its tags."
                )
        ));

        return tags;
    }

    @Cacheable("allTags")
    public Set<String> getAllTags() {

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        0,
                        new ArrayList<>(),
                        "User has accessed all the tags in the system."
                )
        ));

        return fileTagsRepository.findAll().stream().map(FileTag::getTag).collect(Collectors.toSet());
    }

    @Cacheable(value = "filesByTag",key = "#tags")
    public List<ReturnedFileDTO> getFilesByTag(List<String> tags) {
        Set<FileEntity> uniqueFileEntities = new HashSet<>();
        for (String tag : tags) {
            Set<FileTag> fileTags = fileTagsRepository.findByTags(tag);
            if (fileTags == null) { continue; }
            uniqueFileEntities.addAll(fileRepository.findFilesByTags(fileTags));
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        uniqueFileEntities.size(),
                        uniqueFileEntities.stream()
                                .map(f -> f.getPath() + "/"+f.getFilename())
                                .collect(Collectors.toList()),
                        "User has accessed the files based on the provided tags" + tags
                )
        ));

        return uniqueFileEntities.stream()
                .map(file -> fileConvertor.convert(file))
                .collect(Collectors.toList());
    }

    @Transactional
    public String addTagsToFile(String path, String filename,String extension, List<Tag> tags) {

        Cache cache = cacheManager.getCache("tags");
        if(cache != null && cache.get(path + "/" + filename) != null){
            cache.evict(path + "/" + filename);
        }
        Objects.requireNonNull(cacheManager.getCache("allTags")).clear();
        Cache cache1 = cacheManager.getCache("filesByTag");
        if(cache1 != null && cache1.get(tags) != null){
            cache1.evict(tags);
        }

        FileEntity oldFileEntity = fileRepository.getFileByPathAndFilenameAndExtension(path, filename,extension);
        boolean success = true;

        if (oldFileEntity == null) {
            success = false;
        } else {
            Set<FileTag> updatedTags = new HashSet<>(oldFileEntity.getTags());

            for (Tag tag : tags) {
                FileTag fileTag = fileTagsRepository.findByTag(tag.getTag().toLowerCase());

                if (fileTag == null) {
                    fileTag = new FileTag();
                    fileTag.setTag(tag.getTag().toLowerCase());
                    fileTag.setFileEntities(new HashSet<>());
                }

                fileTag.getFileEntities().add(oldFileEntity);
                updatedTags.add(fileTag);
            }

            oldFileEntity.setTags(updatedTags);
            fileRepository.save(oldFileEntity);
        }

        String description;
        if (!success) {
            description = "User has tried to set tags to the file but the file " + path + "/" + filename + " was not found.";
        } else {
            description = "User has set the following tags to the file: " + tags.stream().map(Tag::getTag).collect(Collectors.joining(", "));
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        description
                )
        ));

        return success ? "Tags added successfully" : null;
    }

    @Transactional
    public String addMultipleTags(List<TagPathNameDTO> tagPathNameDTO) {
        for(TagPathNameDTO dto : tagPathNameDTO) {
            String partialResult = addTagsToFile(dto.getPath(), dto.getFilename(),dto.getExtension(), dto.getTags());

            if (partialResult == null) {
                throw new RuntimeException("Failed to add tags for file: "
                        + dto.getFilename() + " at path: " + dto.getPath());
            }
        }

        return "Tags added successfully";
    }

    @Transactional
    public String deleteTagsForFile(String path, String filename,String extension, List<Tag> tags) {

        Cache cache = cacheManager.getCache("tags");
        if(cache != null && cache.get(path + "/" + filename) != null){
            cache.evict(path + "/" + filename);
        }
        Objects.requireNonNull(cacheManager.getCache("allTags")).clear();
        Cache cache1 = cacheManager.getCache("filesByTag");
        if(cache1 != null && cache1.get(tags) != null){
            cache1.evict(tags);
        }

        FileEntity oldFileEntity = fileRepository.getFileByPathAndFilenameAndExtension(path, filename,extension);
        boolean success = true;
        if (oldFileEntity != null) {
            Set<FileTag> fileTags = oldFileEntity.getTags();

            Set<FileTag> tagsToRemove = fileTags.stream()
                    .filter(fileTag -> tags.stream()
                            .anyMatch(tag -> tag.getTag().equals(fileTag.getTag())))
                    .collect(Collectors.toSet());

            fileTags.removeAll(tagsToRemove);
            tagsToRemove.forEach(fileTag -> {
                if (fileTag.getFileEntities().isEmpty()) {
                    fileTagsRepository.delete(fileTag);
                }
            });

            oldFileEntity.setTags(fileTags);
            fileRepository.save(oldFileEntity);
        }else{
            success = false;
        }
        String description;
        if(!success){
            description = "User has tried to delete tags from the file " + path + "/" + filename + "but file does not exist.";
        }else{
            description = "User has deleted the following tags from the file: " + tags;
        }
        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        0,
                        new ArrayList<>(),
                        description
                )
        ));

        return success ? "Tags deleted successfully" : null;
    }

    @Transactional
    public String deleteAllTagsForFile(String path, String filename,String extension) {

        Cache cache = cacheManager.getCache("tags");
        if(cache != null && cache.get(path + "/" + filename) != null){
            cache.evict(path + "/" + filename);
        }
        Objects.requireNonNull(cacheManager.getCache("allTags")).clear();

        FileEntity oldFileEntity = fileRepository.getFileByPathAndFilenameAndExtension(path, filename,extension);
        String description;
        boolean succeeded = false;
        if (oldFileEntity != null) {
            oldFileEntity.getTags().clear();
            fileRepository.save(oldFileEntity);
            succeeded = true;
        }
        if (!succeeded) {
            description="User tried to delete all the tags from the file " + path + "/" + filename + "but file does not exist.";
        }else{
            description = "User has deleted all the tags from the file.";
        }
        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        1,
                        new ArrayList<>(),
                        description
                )
        ));

        return succeeded ? "Tags deleted successfully" : null;
    }

    private void evictCache(String path, String filename, String extension){
        Cache cacheContents = cacheManager.getCache("file");
        if (cacheContents != null) {
            cacheContents.evict(List.of(
                    path,
                    filename,
                    extension
            ));
        }
    }

}
