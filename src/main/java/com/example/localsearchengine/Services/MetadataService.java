package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.KeyDTO;
import com.example.localsearchengine.DTOs.MetadataEntries;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.Metadata;
import com.example.localsearchengine.Persistence.FileRepository;
import com.example.localsearchengine.Persistence.MetadataRepository;
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

    public Metadata getMetadataForFile(String id){return metadataRepository.getMetadataForFile(id);}

    public Metadata getMetadataForFile(String path, String filename){
        Integer fileId = fileRepository.getFileIdByPathAndFilename(path, filename);
        if(fileId == null){return null;}
        return metadataRepository.getMetadataForFile(String.valueOf(fileId));
    }

    public List<Metadata> getMetadataForFiles(List<String> fileIds){return metadataRepository.getMetadataForFiles(fileIds);}

    @Transactional
    public String modifyMetadataForFile(String id, List<MetadataEntries> entries) {
        Metadata metadata = getMetadataForFile(id);
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
    public String modifyMetadataForFile(String path,String filename, List<MetadataEntries> entries) {
        Integer fileId = fileRepository.getFileIdByPathAndFilename(path, filename);
        if(fileId == null){return null;}
        Metadata metadata = getMetadataForFile(String.valueOf(fileId));
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
    public String addMetadataForFile(String id, List<MetadataEntries> entry) {
        Metadata metadata = metadataRepository.getMetadataForFile(id);
        Map<String, String> entries = entry.stream()
                .collect(Collectors.toMap(MetadataEntries::getKey, MetadataEntries::getValue));

        if (metadata == null) {
            Optional<File> file = fileRepository.findById(id);
            if (file.isPresent()) {
                Metadata newMetadata = new Metadata();
                newMetadata.setFile(file.get());
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
    public String deleteMetadataForFile(String id,List<KeyDTO> keys){
        Metadata metadata = metadataRepository.getMetadataForFile(id);
        if(metadata == null){return null;}
        for(KeyDTO key : keys){
            metadata.getValues().remove(key.getKey());
        }
        metadataRepository.save(metadata);
        return "Metadata deleted";
    }

    @Transactional
    public String deleteMetadataForFile(String path,String filename,List<KeyDTO> keys){
        Integer fileId = fileRepository.getFileIdByPathAndFilename(path, filename);
        if(fileId == null){return null;}
        Metadata metadata = metadataRepository.getMetadataForFile(String.valueOf(fileId));
        if(metadata == null){return null;}
        for(KeyDTO key : keys){
            metadata.getValues().remove(key.getKey());
        }
        metadataRepository.save(metadata);
        return "Metadata deleted";
    }
}
