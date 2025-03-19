package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.FileTypeDTO;
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

    public List<FileType> getFileTypesMatching(FileTypeDTO fileTypeDTO) {
        List<FileType> fileTypes = getFileTypes();
        List<FileType> finalFileTypes = new ArrayList<>();
        for (FileType fileType : fileTypes) {
            if(fileType.getType().contains(fileTypeDTO.getType())) {
                finalFileTypes.add(fileType);
            }
        }
        return finalFileTypes;
    }

    @Transactional
    public String saveFileType(FileTypeDTO fileTypeDTO) {
        FileType fileType = new FileType();
        fileType.setType(fileTypeDTO.getType());

        fileTypeRepository.save(fileType);

        return "FileType saved successfully";
    }


    @Transactional
    public String deleteFileType(FileTypeDTO fileTypeDTO) {
        List<FileType> fileTypes = getFileTypes();
        for (FileType fileType : fileTypes) {
            if(fileType.getType().equals(fileTypeDTO.getType())) {
                fileTypeRepository.delete(fileType);
            }
        }
        return "FileType deleted successfully";
    }

    public Boolean checkFileType(FileTypeDTO fileTypeDTO) {
        return fileTypeRepository.existsByFileType(fileTypeDTO.getType());
    }
}
