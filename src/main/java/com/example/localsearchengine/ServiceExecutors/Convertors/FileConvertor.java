package com.example.localsearchengine.ServiceExecutors.Convertors;

import com.example.localsearchengine.DTOs.FileDTOS.ReturnedFileDTO;
import com.example.localsearchengine.Entities.FileEntity;

public class FileConvertor implements Convertor<FileEntity,ReturnedFileDTO> {

    @Override
    public ReturnedFileDTO convert(FileEntity fileEntity) {
        if(fileEntity == null) return null;
        ReturnedFileDTO returnedFileDTO = new ReturnedFileDTO();
        returnedFileDTO.setPath(fileEntity.getPath());
        returnedFileDTO.setFilename(fileEntity.getFilename());
        returnedFileDTO.setExtension(fileEntity.getType().getType());
        return returnedFileDTO;
    }
}
