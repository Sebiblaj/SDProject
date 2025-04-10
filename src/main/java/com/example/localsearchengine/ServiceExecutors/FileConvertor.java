package com.example.localsearchengine.ServiceExecutors;

import com.example.localsearchengine.DTOs.FileDTOS.ReturnedFileDTO;
import com.example.localsearchengine.Entites.File;

import java.util.ArrayList;
import java.util.List;

public class FileConvertor implements Convertor<File,ReturnedFileDTO> {

    @Override
    public ReturnedFileDTO convert(File file) {
        ReturnedFileDTO returnedFileDTO = new ReturnedFileDTO();
        returnedFileDTO.setPath(file.getPath());
        returnedFileDTO.setFilename(file.getFilename());
        returnedFileDTO.setType(file.getType().getType());
        returnedFileDTO.setExtension(file.getExtension());
        returnedFileDTO.setFilesize(file.getFilesize());
        returnedFileDTO.setAccessedAt(file.getAccessedAt());
        returnedFileDTO.setCreatedAt(file.getCreatedAt());
        returnedFileDTO.setUpdatedAt(file.getUpdatedAt());
        List<String> tags = new ArrayList<>();
        file.getTags().forEach(t-> tags.add(t.getTag()));
        returnedFileDTO.setTags(tags);
        return returnedFileDTO;
    }
}
