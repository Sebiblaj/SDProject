package com.example.localsearchengine.ServiceExecutors.Convertors;

import com.example.localsearchengine.DTOs.FileDTOS.ReturnedFileDTO;
import com.example.localsearchengine.Entites.File;

import java.util.ArrayList;
import java.util.List;

public class FileConvertor implements Convertor<File,ReturnedFileDTO> {

    @Override
    public ReturnedFileDTO convert(File file) {
        if(file == null) return null;
        ReturnedFileDTO returnedFileDTO = new ReturnedFileDTO();
        returnedFileDTO.setPath(file.getPath());
        returnedFileDTO.setFilename(file.getFilename());
        returnedFileDTO.setExtension(file.getType().getType());
        return returnedFileDTO;
    }
}
