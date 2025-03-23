package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.*;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileTags;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Persistence.FileRepository;
import com.example.localsearchengine.Persistence.FileTagsRepository;
import com.example.localsearchengine.Persistence.FileTypeRepository;
import com.example.localsearchengine.Persistence.MetadataRepository;
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
    private MetadataRepository metadataRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "logs";

    @Autowired
    public void KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }

    public List<File> getFiles() {
        sendMessage("The user has accessed the files list");
        return fileRepository.findAll();
    }

    public FileType getFileTypeByFileId(String id){
        Optional<File> file = fileRepository.findById(id);
        return file.map(File::getType).orElse(null);
    }

    public FileType getFileTypeByFilePath(String path,String filename){
        File file = fileRepository.getFileByPathAndFilename(path, filename);
        return file != null ? file.getType() : null;
    }

    public File getFileById(String id){return fileRepository.findById(id).orElse(null);}

    public File getFileById(String path,String filename){return fileRepository.getFileByPathAndFilename(path, filename);}

    @Transactional
    public File addFile(FileDTO file) {
        File newFile = new File();
        newFile.setFilename(file.getFilename());
        newFile.setPath(file.getPath());
        List<FileTags> tags = new ArrayList<>();
        for(String tag : file.getTags()){
            FileTags fileTags = new FileTags();
            fileTags.setTag(tag);
            tags.add(fileTags);
        }
        newFile.setTags(tags);
        newFile.setFilesize(file.getFilesize());
        newFile.setAccessedAt(file.getAccessedAt());
        newFile.setCreatedAt(file.getCreatedAt());
        newFile.setExtension(file.getExtension());
        newFile.setUpdatedAt(file.getUpdatedAt());

        FileType fileType = fileTypeRepository.getFileTypeByType(file.getType());
        if(fileType==null){return null;}
        newFile.setType(fileType);
        return this.fileRepository.save(newFile);
    }

    @Transactional
    public String addMultipleFiles(List<FileDTO> files) {
        if (files.isEmpty()) {
            return "No files provided";
        }

        List<String> filenames = files.stream().map(FileDTO::getFilename).collect(Collectors.toList());
        List<String> paths = files.stream().map(FileDTO::getPath).collect(Collectors.toList());

        List<File> existingFiles = fileRepository.findFilesByPathAndFilename(paths,filenames);

        Set<String> existingFileKeys = existingFiles.stream()
                .map(file -> file.getPath() + "|" + file.getFilename())
                .collect(Collectors.toSet());

        Map<String, FileType> fileTypeMap = fileTypeRepository.findAll().stream()
                .collect(Collectors.toMap(FileType::getType, fileType -> fileType));

        List<File> newFiles = new ArrayList<>();

        for (FileDTO file : files) {
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

    @Transactional
    public File updateFile(String id, List<MetadataEntries> request) {
        File oldFile = fileRepository.findById(id).orElse(null);
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
    public String deleteFile(String id) {
        if(fileRepository.findById(id).isPresent()) {
            return "File not found";
        }
        fileRepository.deleteById(id);
        return "File deleted";
    }

    @Transactional
    public String deleteFile(String path,String filename) {
        if(fileRepository.getFileByPathAndFilename(path, filename) != null) {
            return "File not found";
        }
        fileRepository.deleteByPathAndFilename(path,filename);
        return "File deleted";
    }

    public List<File> searchFilesByName(String filename) { return fileRepository.findAllByFilename(filename);}

    public List<File> searchFilesByExtension(String ext) { return fileRepository.findAllByExtension(ext); }

    @Transactional
    public boolean deleteMultipleFiles(List<FileIdDTO> files) {
        List<String> fileIds = files.stream().map(FileIdDTO::getId).collect(Collectors.toList());
        metadataRepository.deleteMetadataForFiles(fileIds);
        fileRepository.deleteAllById(fileIds); return !files.isEmpty();
    }

    @Transactional
    public boolean deleteByPathAndFilename(List<PathAndName> files) {
        int deletedCount = fileRepository.deleteByPathAndFilename(files);
        return deletedCount == files.size();
    }

    public List<File> findBySizeInterval(int min, int max) { return fileRepository.findBySizeGreaterThan(min, max); }


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

    public List<Tag> getTagsForFile(String id){
        List<FileTags> fileTags = fileRepository.findTagsForFile(id);
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

    public List<File> getFilesByTag(String tag) {
        Set<String> allTags = getAllTags();
        List<File> files = new ArrayList<>();
        for(String tagName : allTags) {
            if(tagName.contains(tag)){
                return fileRepository.findFilesForTag(tagName);
            }
        }
        return files;
    }

    public List<File> getFilesByTag(List<Tag> tags) {
        List<File> files = new ArrayList<>();
        for(Tag tag : tags) {
            files.addAll(fileRepository.findFilesForTag(tag.getTag()));
        }
        return files;
    }

    @Transactional
    public String addTagsToFile(String id, List<Tag> tags) {
        File oldFile = fileRepository.findById(String.valueOf(id)).orElse(null);
        if (oldFile != null) {
            for(Tag tag : tags) {
                FileTags fileTags = new FileTags();
                fileTags.setTag(tag.getTag());
                fileTags.setFile(oldFile);
                if(fileTagsRepository.findById(String.valueOf(id)).isEmpty()) {
                    fileTagsRepository.save(fileTags);
                }
            }

            return "Tags added";
        }
        return null;
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
    public String deleteTagsForFile(String id, List<Tag> tags) {
        File oldFile = fileRepository.findById(id).orElse(null);
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

    @Transactional
    public String deleteAllTagsForFile(String id) {
        File oldFile = fileRepository.findById(id).orElse(null);
        if (oldFile != null) {
            oldFile.getTags().clear();
            fileRepository.save(oldFile);
            return "Tags removed";
        }
        return null;
    }


}
