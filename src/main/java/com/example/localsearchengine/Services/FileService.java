package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.FileDTOS.FileDTO;
import com.example.localsearchengine.DTOs.FileDTOS.PathAndName;
import com.example.localsearchengine.DTOs.FileDTOS.ReturnedFileDTO;
import com.example.localsearchengine.DTOs.FileDTOS.Tag;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileTags;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Persistence.FileRepository;
import com.example.localsearchengine.Persistence.FileTagsRepository;
import com.example.localsearchengine.Persistence.FileTypeRepository;
import com.example.localsearchengine.ServiceExecutors.FileConvertor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileTagsRepository fileTagsRepository;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileConvertor fileConvertor;

    private static final String TOPIC = "logs";

    @Autowired
    public void KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }

    public List<ReturnedFileDTO> getFiles() {
        sendMessage("The user has accessed the files list");
        List<ReturnedFileDTO> returnedFileList = new ArrayList<>();
        fileRepository.findAll().forEach(file -> {
            returnedFileList.add(fileConvertor.convert(file));
        });

       return returnedFileList;
    }

    public ReturnedFileDTO getFile(String fileName,String filePath){
        return fileConvertor.convert(fileRepository.getFileByPathAndFilename(filePath, fileName));
    }

    @Transactional
    public String addFile(Object payload) {
        if(payload instanceof FileDTO) {
            FileDTO fileDTO = objectMapper.convertValue(payload, FileDTO.class);
            File newFile = new File();
            newFile.setFilename(fileDTO.getFilename());
            newFile.setPath(fileDTO.getPath());
            List<FileTags> tags = new ArrayList<>();
            for (String tag : fileDTO.getTags()) {
                FileTags fileTags = new FileTags();
                fileTags.setTag(tag);
                tags.add(fileTags);
            }
            newFile.setTags(tags);
            newFile.setFilesize(fileDTO.getFilesize());
            newFile.setAccessedAt(fileDTO.getAccessedAt());
            newFile.setCreatedAt(fileDTO.getCreatedAt());
            newFile.setExtension(fileDTO.getExtension());
            newFile.setUpdatedAt(fileDTO.getUpdatedAt());

            FileType fileType = fileTypeRepository.getFileTypeByType(fileDTO.getType());
            if (fileType == null) {
                return null;
            }
            newFile.setType(fileType);
            fileRepository.save(newFile);
            return "File added successfully";
        }else if(payload instanceof List<?>){
            List<FileDTO> fileDTOs = Collections.singletonList(objectMapper.convertValue(payload, FileDTO.class));

            List<String> filenames = fileDTOs.stream().map(FileDTO::getFilename).collect(Collectors.toList());
            List<String> paths = fileDTOs.stream().map(FileDTO::getPath).collect(Collectors.toList());

            List<File> existingFiles = fileRepository.findFilesByPathAndFilename(paths,filenames);

            Set<String> existingFileKeys = existingFiles.stream()
                    .map(file -> file.getPath() + "|" + file.getFilename())
                    .collect(Collectors.toSet());

            Map<String, FileType> fileTypeMap = fileTypeRepository.findAll().stream()
                    .collect(Collectors.toMap(FileType::getType, fileType -> fileType));

            List<File> newFiles = new ArrayList<>();

            for (FileDTO file : fileDTOs) {
                String key = file.getPath() + "|" + file.getFilename();
                if (existingFileKeys.contains(key)) {
                    continue;
                }

                File newFile = new File();
                newFile.setFilename(file.getFilename());
                newFile.setPath(file.getPath());

                List<FileTags> tags = file.getTags().stream()
                        .map(tag -> new FileTags(null,newFile,tag))
                        .toList();
                newFile.setTags(tags);

                newFile.setFilesize(file.getFilesize());
                newFile.setAccessedAt(file.getAccessedAt());
                newFile.setCreatedAt(file.getCreatedAt());
                newFile.setExtension(file.getExtension());
                newFile.setUpdatedAt(file.getUpdatedAt());

                FileType fileType = fileTypeMap.get(file.getType());
                if (fileType == null) {
                    return "Error: FileType '" + file.getType() + "' not found";
                }
                newFile.setType(fileType);

                newFiles.add(newFile);
            }

            if (newFiles.isEmpty()) {
                return "No new files to add";
            }

            fileRepository.saveAll(newFiles);
            return "Files added successfully";
        }

        return null;
    }

    @Transactional
    public File updateFile(String path,String filename, List<MetadataEntries> request) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);
        if (oldFile == null) {
            throw new RuntimeException("File not found!");
        }

        for (MetadataEntries entry : request) {
            String fieldName = entry.getKey();
            String newValue = entry.getValue();

            try {
                Field field = File.class.getDeclaredField(fieldName);
                field.setAccessible(true);

                Object convertedValue = convertValue(field.getType(), newValue);

                field.set(oldFile, convertedValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error updating field: " + fieldName, e);
            }
        }

        return fileRepository.save(oldFile);
    }

    @Transactional
    public String deleteFile(String path,String filename) {
        if(fileRepository.getFileByPathAndFilename(path, filename) != null) {
            return "File not found";
        }
        fileRepository.deleteByPathAndFilename(path,filename);
        return "File deleted";
    }

    public List<ReturnedFileDTO> searchFilesByName(String filename) {
        List<File> files = fileRepository.findAllByFilename(filename);
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if (files.isEmpty()) { return returnedFiles; }
        files.forEach(file -> {
            returnedFiles.add(fileConvertor.convert(file));
        });
        return returnedFiles;
    }

    public List<ReturnedFileDTO> searchFilesByExtension(String ext) {
        List<File> files = fileRepository.findAllByExtension(ext);
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if (files.isEmpty()) { return returnedFiles; }
        files.forEach(file -> {
            returnedFiles.add(fileConvertor.convert(file));
        });
        return returnedFiles;
    }

    @Transactional
    public boolean deleteByPathAndFilename(List<PathAndName> files) {
        int deletedCount = fileRepository.deleteByPathAndFilename(files);
        return deletedCount == files.size();
    }

    public List<ReturnedFileDTO> findBySizeInterval(String min, String max) {
        List<File> files = fileRepository.findBySizeGreaterThan(min, max);
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if (files.isEmpty()) { return returnedFiles; }
        files.forEach(file -> {
            returnedFiles.add(fileConvertor.convert(file));
        });
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

    public List<Tag> getTagsForFile(String path, String filename) {
        List<FileTags> fileTags = fileRepository.findTagsForFile(path, filename);
        if(!fileTags.isEmpty()) {
            List<Tag> tags = new ArrayList<>();
            Tag tag = new Tag();
            fileTags.forEach(fileTag -> {
                tags.add(new Tag(fileTag.getTag()));
            });
            return tags;
        }
        return null;
    }

    public Set<String> getAllTags(){
        return fileTagsRepository.findAll().stream().map(FileTags::getTag).collect(Collectors.toSet());
    }

    public List<ReturnedFileDTO> getFilesByTag(String tag) {
        Set<String> allTags = getAllTags();
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        Map<String,String> pathAndFilename = new HashMap<>();
        for(String tagName : allTags) {
            if(tagName.contains(tag)){
                List<File> repoFiles = fileRepository.findFilesForTag(tagName);
                if(repoFiles.isEmpty()) { continue; }
                repoFiles.forEach(file -> {
                    if(!pathAndFilename.containsKey(file.getPath())){
                        returnedFiles.add(fileConvertor.convert(file));
                        pathAndFilename.put(file.getPath(),file.getFilename());
                    }
                });
            }
        }
        return returnedFiles;
    }

    public List<ReturnedFileDTO> getFilesByTag(List<String> tags) {
        List<ReturnedFileDTO> returnedFileDTOS = new ArrayList<>();
        for(String tag : tags) {
            List<File> files = fileRepository.findFilesForTag(tag);
            if(files.isEmpty()) { continue; }
            files.forEach(file -> {
                returnedFileDTOS.add(fileConvertor.convert(file));
            });
        }
        return returnedFileDTOS;
    }

    @Transactional
    public String addTagsToFile(String path, String filename, List<Tag> tags) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);
        if (oldFile != null) {
            for(Tag tag : tags) {
                FileTags fileTags = new FileTags();
                fileTags.setTag(tag.getTag());
                fileTags.setFile(oldFile);
                if(fileTagsRepository.findById(String.valueOf(oldFile.getId())).isEmpty()) {
                    fileTagsRepository.save(fileTags);
                }
            }

            return "Tags added";
        }
        return null;
    }

    @Transactional
    public String deleteTagsForFile(String path, String filename, List<Tag> tags) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);

        if (oldFile != null) {
            List<FileTags> fileTags = oldFile.getTags();

            fileTags.removeIf(fileTag -> tags.stream()
                    .anyMatch(tag -> tag.getTag().equals(fileTag.getTag())));

            oldFile.setTags(fileTags);
            fileRepository.save(oldFile);

            return "Tags removed";
        }
        return "File not found";
    }

    @Transactional
    public String deleteAllTagsForFile(String path, String filename) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);

        if (oldFile != null) {
            oldFile.getTags().clear();
            fileRepository.save(oldFile);
            return "Tags removed";
        }

        return null;
    }


}
