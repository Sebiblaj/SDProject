package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.TagsList;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileTags;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Persistence.FileRepository;
import com.example.localsearchengine.DTOs.PathAndName;
import com.example.localsearchengine.Persistence.FileTagsRepository;
import com.example.localsearchengine.Persistence.FileTypeRepository;
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

    public File getFileById(int id){return fileRepository.findById(String.valueOf(id)).orElse(null);}

    @Transactional
    public File addFile(File file) { return this.fileRepository.save(file);}

    @Transactional
    public String addMultipleFiles(List<File> files) {
        this.fileRepository.saveAll(files) ;
        return files.size() + " files added";
    }

    @Transactional
    public File updateFile(int id, Map<String, String> request) {
        File oldFile = fileRepository.findById(String.valueOf(id)).orElse(null);
        if (oldFile == null) {
            throw new RuntimeException("File not found!");
        }

        for (Map.Entry<String, String> entry : request.entrySet()) {
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
    public String deleteFileById(int id) {
        if(fileRepository.findById(String.valueOf(id)).isEmpty()) {
            return "File not found";
        }
        fileRepository.deleteById(String.valueOf(id));
        return "File deleted";
    }

    public List<File> searchFilesByName(String filename) { return fileRepository.findAllByFilename(filename);}

    public List<File> searchFilesByExtension(String ext) { return fileRepository.findAllByExtension(ext); }

    @Transactional
    public boolean deleteMultipleFiles(List<String> files) { fileRepository.deleteAllById(files); return !files.isEmpty();}

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

    public TagsList getTagsForFile(String id){
        List<FileTags> fileTags = fileRepository.findTagsForFile(id);
        if(!fileTags.isEmpty()) {
            TagsList tagsList = new TagsList();
            fileTags.forEach(fileTag -> {
                tagsList.getTags().add(fileTag.getTag());
            });
            return tagsList;
        }
        return null;
    }

    public TagsList getTagsForFile(String path, String filename) {
        List<FileTags> fileTags = fileRepository.findTagsForFile(path, filename);
        if(!fileTags.isEmpty()) {
            TagsList tagsList = new TagsList();
            fileTags.forEach(fileTag -> {
                tagsList.getTags().add(fileTag.getTag());
            });
            return tagsList;
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

    public List<File> getFilesByTag(TagsList tags) {
        List<File> files = new ArrayList<>();
        for(String tagName : tags.getTags()) {
            files.addAll(fileRepository.findFilesForTag(tagName));
        }
        return files;
    }

    @Transactional
    public String addTagsToFile(String id,TagsList tags) {
        File oldFile = fileRepository.findById(String.valueOf(id)).orElse(null);
        if (oldFile != null) {
            for(String tagName : tags.getTags()) {
                FileTags fileTags = new FileTags(null,oldFile,tagName);
                if(fileTagsRepository.findById(String.valueOf(id)).isEmpty()) {
                    fileTagsRepository.save(fileTags);
                }
            }

            return "Tags added";
        }
        return null;
    }

    @Transactional
    public String addTagsToFile(String path,String filename,TagsList tags) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);
        if (oldFile != null) {
            for(String tagName : tags.getTags()) {
                FileTags fileTags = new FileTags(null,oldFile,tagName);
                if(fileTagsRepository.findById(String.valueOf(oldFile.getId())).isEmpty()) {
                    fileTagsRepository.save(fileTags);
                }
            }

            return "Tags added";
        }
        return null;
    }

    @Transactional
    public String deleteTagsForFile(String path, String filename, TagsList tags) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);

        if (oldFile != null) {
            List<FileTags> fileTags = oldFile.getTags();

            fileTags.removeIf(fileTag -> tags.getTags().contains(fileTag.getTag()));

            oldFile.setTags(fileTags);
            fileRepository.save(oldFile);

            return "Tags removed";
        }
        return "File not found";
    }

    @Transactional
    public String deleteTagsForFile(String id,TagsList tags) {
        File oldFile = fileRepository.findById(id).orElse(null);
        if (oldFile != null) {
            List<FileTags> fileTags = oldFile.getTags();

            fileTags.removeIf(fileTag -> tags.getTags().contains(fileTag.getTag()));

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

    @Transactional
    public String replaceTagsForFile(String path, String filename, TagsList tags) {
        File oldFile = fileRepository.getFileByPathAndFilename(path, filename);
        if (oldFile != null) {
            oldFile.getTags().clear();
            List<FileTags> fileTags = new ArrayList<>();
            for(String tag: tags.getTags()) {
                FileTags fileTag = new FileTags(null,oldFile,tag);
                fileTags.add(fileTag);
            }
            oldFile.setTags(fileTags);
            fileRepository.save(oldFile);
            return "Tags replaced";
        }
        return null;
    }

    @Transactional
    public String replaceTagsForFile(String id, TagsList tags) {
        File oldFile = fileRepository.findById(id).orElse(null);
        if (oldFile != null) {
            oldFile.getTags().clear();
            List<FileTags> fileTags = new ArrayList<>();
            for(String tag: tags.getTags()) {
                FileTags fileTag = new FileTags(null,oldFile,tag);
                fileTags.add(fileTag);
            }
            oldFile.setTags(fileTags);
            fileRepository.save(oldFile);
            return "Tags replaced";
        }
        return null;
    }


}
