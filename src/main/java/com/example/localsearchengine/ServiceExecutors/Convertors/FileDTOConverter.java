package com.example.localsearchengine.ServiceExecutors.Convertors;

import com.example.localsearchengine.DTOs.FileDTOS.FileDTO;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileTag;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Entites.Metadata;
import com.example.localsearchengine.Persistence.FileTagsRepository;
import com.example.localsearchengine.Persistence.FileTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FileDTOConverter implements Convertor<FileDTO, File> {

    @Autowired
    private FileTagsRepository fileTagsRepository;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Override
    public File convert(FileDTO fileDTO) {
        File newFile = new File();
        newFile.setFilename(fileDTO.getFilename());
        newFile.setPath(fileDTO.getPath());

        FileType fileType = fileTypeRepository.getFileTypeByType(fileDTO.getType());
        newFile.setType(fileType);

        Set<FileTag> tags = new HashSet<>();
        for (String tagName : fileDTO.getTags()) {
            FileTag tag = fileTagsRepository.findByTag(tagName);
            if (tag == null) {
                tag = new FileTag();
                tag.setTag(tagName);
                tag.setFiles(new HashSet<>());
                fileTagsRepository.save(tag);
            }
            tag.getFiles().add(newFile);
            tags.add(tag);
        }

        newFile.setTags(tags);

        Map<String, String> stringMetadata = fileDTO.getMetadata().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().toString()
                ));
        Metadata metadata = new Metadata(null, stringMetadata, newFile);

        newFile.setMetadata(metadata);

        System.out.println("File Converted successfully");
        return newFile;
    }
}

