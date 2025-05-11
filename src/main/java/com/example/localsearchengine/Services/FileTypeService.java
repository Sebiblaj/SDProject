package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.FileDTOS.FileTypeDTO;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Persistence.FileTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FileTypeService {

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private CacheManager cacheManager;

    @Cacheable("fileTypesNoId")
    public List<FileTypeDTO> getFileTypesNoId() {
        List<FileType> fileTypes = fileTypeRepository.findAll();
        List<FileTypeDTO> fileTypeDTOs = new ArrayList<>();
        for (FileType fileType : fileTypes) {
            FileTypeDTO fileTypeDTO = new FileTypeDTO();
            fileTypeDTO.setType(fileType.getType());
            fileTypeDTO.setWeight(fileType.getWeight());
            fileTypeDTOs.add(fileTypeDTO);
        }
        return fileTypeDTOs;
    }

    @Transactional
    public String updateFileType(FileTypeDTO fileTypeDTO) {

        Objects.requireNonNull(cacheManager.getCache("fileTypesNoId")).clear();
        Cache cache = cacheManager.getCache("fileType");
        if(cache != null && cache.get(fileTypeDTO.getType()) != null) {
            cache.evict(fileTypeDTO.getType());
        }

        FileType fileType = fileTypeRepository.getFileTypeByType(fileTypeDTO.getType());
        if(fileType==null) { return null;}
        fileType.setWeight(fileType.getWeight());
        fileTypeRepository.save(fileType);
        return "File Type Updated";
    }

    @Transactional
    public String saveFileType(String ext,Double weight) {

        Objects.requireNonNull(cacheManager.getCache("fileTypesNoId")).clear();
        Objects.requireNonNull(cacheManager.getCache("fileTypes")).clear();

        FileType fileType = new FileType();
        fileType.setType(ext);
        fileType.setWeight(weight);
        fileTypeRepository.save(fileType);

        return "FileType saved successfully";
    }

    @Transactional
    public String deleteFileType(List<String> ext) {
        Objects.requireNonNull(cacheManager.getCache("fileTypesNoId")).clear();

        List<FileType> fileTypes = fileTypeRepository.findAll();
        for (FileType fileType : fileTypes) {
            if(ext.contains(fileType.getType())) {
                Objects.requireNonNull(cacheManager.getCache("fileType")).evict(fileType.getType());
                fileTypeRepository.delete(fileType);
            }
        }
        return "FileType deleted successfully";
    }

    @Cacheable(value = "fileType", key = "{#type}" )
    public Boolean checkFileType(String type) {
        return fileTypeRepository.existsByFileType(type);
    }
}
