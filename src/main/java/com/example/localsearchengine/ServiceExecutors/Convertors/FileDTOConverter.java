package com.example.localsearchengine.ServiceExecutors.Convertors;

import com.example.localsearchengine.DTOs.FileDTOS.FileDTO;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileTag;
import com.example.localsearchengine.Persistence.FileTagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class FileDTOConverter implements Convertor<FileDTO, File> {

    @Autowired
    private FileTagsRepository fileTagsRepository;

    @Override
    public File convert(FileDTO fileDTO) {
        File newFile = new File();
        newFile.setFilename(fileDTO.getFilename());
        newFile.setPath(fileDTO.getPath());
        newFile.setFilesize(fileDTO.getFilesize());
        newFile.setAccessedAt(fileDTO.getAccessedAt());
        newFile.setCreatedAt(fileDTO.getCreatedAt());
        newFile.setExtension(fileDTO.getExtension());
        newFile.setUpdatedAt(fileDTO.getUpdatedAt());

        Set<FileTag> tags = new HashSet<>();
        for (String tagName : fileDTO.getTags()) {
            FileTag tag = fileTagsRepository.findByTag(tagName);
            if(tag == null) {
                tag = new FileTag();
                tag.setTag(tagName);
                tag.setFiles(new HashSet<>());
            }
            tag.getFiles().add(newFile);
            tags.add(tag);
        }

        newFile.setTags(tags);
        return newFile;
    }
}
