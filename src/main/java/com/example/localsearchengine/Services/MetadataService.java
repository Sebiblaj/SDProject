package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.MetadataDTOS.KeyDTO;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataDTO;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.Metadata;
import com.example.localsearchengine.Persistence.FileRepository;
import com.example.localsearchengine.Persistence.MetadataRepository;
import com.example.localsearchengine.ServiceExecutors.MetadataConvertor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetadataService {

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private MetadataConvertor metadataConvertor;

    public MetadataDTO getMetadataForFile(String path, String filename){
        return metadataConvertor.convert(metadataRepository.getMetadataForFile(path, filename));
    }

    @Transactional
    public String modifyMetadataForFile(String path,String filename, List<MetadataEntries> entries) {
        Metadata metadata = metadataRepository.getMetadataForFile(path, filename);
        if(metadata == null){return null;}
        for(MetadataEntries entry : entries){
            if(metadata.getValues().containsKey(entry.getKey())){
                metadata.getValues().replace(entry.getKey(), entry.getValue());
            }
        }
        metadataRepository.save(metadata);
        return "Metadata modified";
    }

    @Transactional
    public String addMetadataForFile(String path, String filename, List<MetadataEntries> entry) {
        Metadata metadata = metadataRepository.getMetadataForFile(path,filename);
        Map<String, String> entries = entry.stream()
                .collect(Collectors.toMap(MetadataEntries::getKey, MetadataEntries::getValue));

        if (metadata == null) {
            File file = fileRepository.findFilesByPathAndFilename(path, filename);
            if (file != null) {
                Metadata newMetadata = new Metadata();
                newMetadata.setFile(file);
                newMetadata.setValues(entries);

                metadataRepository.save(newMetadata);
            } else {
                return null;
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
        return "Metadata added";
    }

    @Transactional
    public String deleteMetadataForFile(String path,String filename,List<KeyDTO> keys){
        Metadata metadata = metadataRepository.getMetadataForFile(path, filename);
        if(metadata == null){return null;}
        for(KeyDTO key : keys){
            metadata.getValues().remove(key.getKey());
        }
        metadataRepository.save(metadata);
        return "Metadata deleted";
    }
}
