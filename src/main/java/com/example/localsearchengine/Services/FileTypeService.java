package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.FileDTOS.FileTypeDTO;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Persistence.FileTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileTypeService {

    @Autowired
    private FileTypeRepository fileTypeRepository;

    public List<FileType> getFileTypes() {return fileTypeRepository.findAll();}

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
        FileType fileType = fileTypeRepository.getFileTypeByType(fileTypeDTO.getType());
        if(fileType==null) { return null;}
        fileType.setWeight(fileType.getWeight());
        fileTypeRepository.save(fileType);
        return "File Type Updated";
    }

    @Transactional
    public String saveFileType(String ext,Double weight) {
        FileType fileType = new FileType();
        fileType.setType(ext);
        fileType.setWeight(weight);
        fileTypeRepository.save(fileType);

        return "FileType saved successfully";
    }

    @Transactional
    public String deleteFileType(List<String> ext) {
        List<FileType> fileTypes = getFileTypes();
        for (FileType fileType : fileTypes) {
            if(ext.contains(fileType.getType())) {
                fileTypeRepository.delete(fileType);
            }
        }
        return "FileType deleted successfully";
    }

    public Boolean checkFileType(String type) {
        return fileTypeRepository.existsByFileType(type);
    }
}
