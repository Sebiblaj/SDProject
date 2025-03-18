package com.example.localsearchengine.Services;

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

    public List<FileType> getFileTypesMatching(String pattern) {
        List<FileType> fileTypes = getFileTypes();
        List<FileType> finalFileTypes = new ArrayList<FileType>();
        for (FileType fileType : fileTypes) {
            if(fileType.getType().contains(pattern)) {
                finalFileTypes.add(fileType);
            }
        }
        return finalFileTypes;
    }

    @Transactional
    public String saveFileType(String type) {
        FileType fileType = new FileType(null,type);
        fileTypeRepository.save(fileType);
        return "FileType saved successfully";
    }

    @Transactional
    public String deleteFileType(String type) {
        List<FileType> fileTypes = getFileTypes();
        for (FileType fileType : fileTypes) {
            if(fileType.getType().equals(type)) {
                fileTypeRepository.delete(fileType);
            }
        }
        return "FileType deleted successfully";
    }
}
