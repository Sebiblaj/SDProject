package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.FileDTOS.*;
import com.example.localsearchengine.DTOs.LoggerMessage;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileTag;
import com.example.localsearchengine.Entites.FileType;
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

    public ReturnedFileDTO getFile(String fileName,String filePath){
        File file = fileRepository.getFileByPathAndFilename(filePath, fileName);

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        file != null ? 1 : 0,
                        file != null ? new ArrayList<>(List.of(file.getPath() + "/" + file.getFilename())) : new ArrayList<>(),
                        "User has accessed the file."
                )
        ));
        return fileConvertor.convert(file);
    }

    @Transactional
    public String addFile(Object payload) {
        if (payload instanceof List<?>) {
            boolean success=true;
            List<FileDTO> fileDTOs = objectMapper.convertValue(payload, new TypeReference<>() {});

            List<String> filenames = fileDTOs.stream().map(FileDTO::getFilename).toList();
            List<String> paths = fileDTOs.stream().map(FileDTO::getPath).toList();

            List<File> existingFiles = fileRepository.findFilesByPathAndFilename(paths, filenames);

            Set<String> existingFileKeys = existingFiles.stream()
                    .map(file -> file.getPath() + "|" + file.getFilename())
                    .collect(Collectors.toSet());

            Map<String, FileType> fileTypeMap = fileTypeRepository.findAll().stream()
                    .collect(Collectors.toMap(FileType::getType, fileType -> fileType));

            List<File> newFiles = new ArrayList<>();

            for (FileDTO fileDTO : fileDTOs) {
                String key = fileDTO.getPath() + "|" + fileDTO.getFilename();
                if (existingFileKeys.contains(key)) {
                    continue;
                }

                File newFile = fileDTOConverter.convert(fileDTO);

                FileType fileType = fileTypeMap.get(fileDTO.getType());
                if (fileType == null) {
                    success = false;
                    break;
                }
                newFile.setType(fileType);
                newFiles.add(newFile);
            }

            if (newFiles.isEmpty()) {
                success = false;
            }else{

                fileRepository.saveAll(newFiles);
            }

            String description;
            List<String> filesToAdd = new ArrayList<>();
            if (success) {
                description = "User has set multiple files into the system.";
                filesToAdd =newFiles.stream().map(f ->  f.getPath() + "/"+f.getFilename()).toList();
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
            File newFile = fileDTOConverter.convert(fileDTO);

            System.out.println("Adding file " + newFile);
            fileRepository.save(newFile);

            sendMessage(new LoggerMessage(
                    Timestamp.valueOf(LocalDateTime.now()),
                    "sebir",
                    new ActivityDetails(
                            1,
                            new ArrayList<>(List.of(newFile.getPath() + "/" + newFile.getFilename())),
                            "User has set a file into the system."
                    )
            ));

            return "File added successfully";

        }
        return null;
    }

    @Transactional
    public String updateFile(String path,String filename, List<MetadataEntries> request) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);
        boolean success = true;
        if (oldFile == null) {
            success = false;
        }else {

            for (MetadataEntries entry : request) {
                String fieldName = entry.getKey();
                String newValue = entry.getValue();

                try {
                    Field field = File.class.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    Object convertedValue = convertValue(field.getType(), newValue);

                    field.set(oldFile, convertedValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    success = false;
                }
            }
        }

        String description;
        String filePath;
        String fileName;
        if(success){
            description = "User has updated all the files from the system.";
            filePath = oldFile.getPath();
            fileName = oldFile.getFilename();
            fileRepository.save(oldFile);
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

        return success ? "File updated successfully" : "File not found";
    }

    @Transactional
    public List<ReturnedFileDTO> searchFilesByName(String filename) {
        System.out.println("Filename: " + filename);
        List<File> files = fileRepository.findAllByFilename(filename);
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if ( files == null) {
            System.out.println("empty");
            return returnedFiles; }
        files.forEach(file -> {
            returnedFiles.add(fileConvertor.convert(file));
        });

        System.out.println("returned files: " + files);
        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        returnedFiles.size(),
                        returnedFiles.stream()
                                .map(f-> f.getPath() + "/"+f.getFilename())
                                .collect(Collectors.toList()),
                        "User has accessed all the files with the same name in the system."
                )
        ));

        return returnedFiles;
    }

    public List<ReturnedFileDTO> searchFilesByExtension(String ext) {
        List<File> files = fileRepository.findAllByExtension(ext);
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if (files.isEmpty()) { return returnedFiles; }
        files.forEach(file -> {
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
                        "User has accessed all the files with the same extension in the system."
                )
        ));
        return returnedFiles;
    }

    @Transactional
    public boolean deleteByPathAndFilename(List<PathAndName> files) {
        int deletedCount = fileRepository.deleteByPathAndFilename(files);
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
        List<File> files = fileRepository.findAll();
        int size = files.size();
        List<String> names = files.stream().map(File::getFilename).toList();
        List<String> paths = files.stream().map(File::getPath).toList();
        for(int i = 0 ; i< names.size() ; i++){
            deleteAllTagsForFile(paths.get(i), names.get(i));

        }
        fileRepository.deleteAll();

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        size,
                        names,
                        "User has deleted all the files in the system."
                )
        ));
    }

    public List<ReturnedFileDTO> findBySizeInterval(String min, String max) {
        List<File> files = fileRepository.findByFileSizeBetween(Long.parseLong(min), Long.parseLong(max));
        List<ReturnedFileDTO> returnedFiles = new ArrayList<>();
        if (files.isEmpty()) { return returnedFiles; }
        files.forEach(file -> {
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
                        "User has accessed the files within interval [" + min + ", " + max + "] from the system."
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

    public List<ReturnedFileDTO> getFilesByTag(List<String> tags) {
        Set<File> uniqueFiles = new HashSet<>();
        for (String tag : tags) {
            Set<FileTag> fileTags = fileTagsRepository.findByTags(tag);
            if (fileTags == null) { continue; }
            uniqueFiles.addAll(fileRepository.findFilesByTags(fileTags));
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new ActivityDetails(
                        uniqueFiles.size(),
                        uniqueFiles.stream()
                                .map(f -> f.getPath() + "/"+f.getFilename())
                                .collect(Collectors.toList()),
                        "User has accessed the files based on the provided tags" + tags
                )
        ));

        return uniqueFiles.stream()
                .map(file -> fileConvertor.convert(file))
                .collect(Collectors.toList());
    }

    @Transactional
    public String addTagsToFile(String path, String filename, List<Tag> tags) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);  // Find the file
        boolean success = true;

        if (oldFile == null) {
            success = false;
        } else {
            Set<FileTag> updatedTags = new HashSet<>(oldFile.getTags());

            for (Tag tag : tags) {
                FileTag fileTag = fileTagsRepository.findByTag(tag.getTag().toLowerCase());

                if (fileTag == null) {
                    fileTag = new FileTag();
                    fileTag.setTag(tag.getTag().toLowerCase());
                    fileTag.setFiles(new HashSet<>());
                }

                fileTag.getFiles().add(oldFile);
                updatedTags.add(fileTag);
            }

            oldFile.setTags(updatedTags);
            fileRepository.save(oldFile);
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
            String partialResult = addTagsToFile(dto.getPath(), dto.getFilename(), dto.getTags());

            if (partialResult == null) {
                throw new RuntimeException("Failed to add tags for file: "
                        + dto.getFilename() + " at path: " + dto.getPath());
            }
        }

        return "Tags added successfully";
    }

    @Transactional
    public String deleteTagsForFile(String path, String filename, List<Tag> tags) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);
        boolean success = true;
        if (oldFile != null) {
            Set<FileTag> fileTags = oldFile.getTags();

            Set<FileTag> tagsToRemove = fileTags.stream()
                    .filter(fileTag -> tags.stream()
                            .anyMatch(tag -> tag.getTag().equals(fileTag.getTag())))
                    .collect(Collectors.toSet());

            fileTags.removeAll(tagsToRemove);
            tagsToRemove.forEach(fileTag -> {
                if (fileTag.getFiles().isEmpty()) {
                    fileTagsRepository.delete(fileTag);
                }
            });

            oldFile.setTags(fileTags);
            fileRepository.save(oldFile);
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
    public String deleteAllTagsForFile(String path, String filename) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);
        String description;
        boolean succeeded = false;
        if (oldFile != null) {
            oldFile.getTags().clear();
            fileRepository.save(oldFile);
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

}
