package com.example.localsearchengine.Services;

import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Persistence.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

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

}
