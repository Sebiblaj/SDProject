package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.LoggerMessage;
import com.example.localsearchengine.DTOs.MetadataDTOS.KeyDTO;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataDTO;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataPathNameDTO;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.Metadata;
import com.example.localsearchengine.Persistence.FileRepository;
import com.example.localsearchengine.Persistence.MetadataRepository;
import com.example.localsearchengine.ServiceExecutors.Convertors.MetadataConvertor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetadataService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private MetadataConvertor metadataConvertor;

    @Value("${kafka.topic.logs}")
    private String TOPIC;

    @Autowired
    public void KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Object message) {
        kafkaTemplate.send(TOPIC, message);
    }

    @Cacheable(value = "metadata", key = "{#path, #filename, #extension}")
    public MetadataDTO getMetadataForFile(String path, String filename,String extension){

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has accessed the metadata for the file."
                )
        ));

        return metadataConvertor.convert(metadataRepository.getMetadataForFile(path, filename,extension));
    }

    @Transactional
    public String modifyMetadataForFile(String path,String filename,String extension, List<MetadataEntries> entries) {
        Metadata metadata = metadataRepository.getMetadataForFile(path, filename,extension);
        boolean success = true;
        if(metadata == null){
            success = false;
        }else {
            for (MetadataEntries entry : entries) {
                if (metadata.getValues().containsKey(entry.getKey())) {
                    metadata.getValues().replace(entry.getKey(), entry.getValue());
                }
            }
            Cache cache = cacheManager.getCache("metadata");
            if (cache != null && cache.get(path + filename + extension) != null) {
                cache.evict(path + filename + extension);
            }
            metadataRepository.save(metadata);
        }
        String description;
        if(success){
            description = "User updated some metadata for the file.";
        }else{
            description = "User has tried to update the metadata for the file: " + path + "/" + filename + " but it has no metadata.";
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        description
                )
        ));

        return success ? "Metadata modified for file" : null;
    }

    @Transactional
    public String addMetadataForFile(String path, String filename,String extension, List<MetadataEntries> entry) {
        Metadata metadata = metadataRepository.getMetadataForFile(path,filename,extension);
        Map<String, String> entries = entry.stream()
                .collect(Collectors.toMap(MetadataEntries::getKey, MetadataEntries::getValue));

        Cache cache = cacheManager.getCache("metadata");
        if (cache != null && cache.get(path + filename + extension) != null) {
            cache.evict(path + filename + extension);
        }

        boolean success = true;
        if (metadata == null) {
            File file = fileRepository.findFilesByPathAndFilename(path, filename);
            if (file != null) {
                Metadata newMetadata = new Metadata();
                newMetadata.setFile(file);
                newMetadata.setValues(entries);

                metadataRepository.save(newMetadata);
            } else {
                success = false;
            }
        } else {
            Map<String, String> existingEntries = metadata.getValues();
            if (existingEntries == null) {
                existingEntries = new HashMap<>();
            }

            existingEntries.putAll(entries);
            metadata.setValues(existingEntries);

            metadataRepository.save(metadata);
        }

        String description;
        if(success){
            description = "User has set new metadata for the file.";
        }else{
            description = "User has tried to set metadata to file: " + path + "/" + filename + " but file does not exist.";
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        description
                )
        ));
        return success ? "Metadata added for file" : null;
    }

    @Transactional
    public String addMultipleMetadata(List<MetadataPathNameDTO> metadataPathNameDTO) {

        for (MetadataPathNameDTO dto : metadataPathNameDTO) {
            List<MetadataEntries> entries = dto.getMetadataDTO().getMetadata().entrySet().stream()
                    .map(entry -> new MetadataEntries(entry.getKey(), (String) entry.getValue()))
                    .toList();

            String partialResult = addMetadataForFile(dto.getPath(), dto.getFilename(),dto.getExtension(), entries);

            if (partialResult == null) {
                throw new RuntimeException("Failed to add metadata for file: "
                        + dto.getFilename() + " at path: " + dto.getPath());
            }

        }

        return "Metadata added successfully";
    }

    @Transactional
    public String deleteMetadataForFile(String path,String filename,String extension,List<KeyDTO> keys){
        Metadata metadata = metadataRepository.getMetadataForFile(path, filename,extension);
        boolean success = true;
        if(metadata == null){
            success = false;
        }else {
            for (KeyDTO key : keys) {
                metadata.getValues().remove(key.getKey());
            }
            metadataRepository.save(metadata);
        }

        Cache cache = cacheManager.getCache("metadata");
        if (cache != null && cache.get(path + filename + extension) != null) {
            cache.evict(path + filename + extension);
        }

        String description;
        if(success){
           description= "User deleted some metadata for the file: " + keys.stream().map(KeyDTO::getKey).toList();
        }else{
            description = "User has tried to delete metadata for file: " + path + "/" + filename + " but file has no metadata";
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        description
                )
        ));

        return success ? "Metadata deleted" : null;
    }
}
