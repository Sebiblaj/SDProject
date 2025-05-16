package com.example.localsearchengine.ServiceExecutors.Convertors;

import com.example.localsearchengine.DTOs.FileDTOS.FileDTO;
import com.example.localsearchengine.Entities.FileEntity;
import com.example.localsearchengine.Entities.FileTag;
import com.example.localsearchengine.Entities.FileType;
import com.example.localsearchengine.Entities.Metadata;
import com.example.localsearchengine.Persistence.FileTagsRepository;
import com.example.localsearchengine.Persistence.FileTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FileDTOConverter implements Convertor<FileDTO, FileEntity> {

    @Autowired
    private FileTagsRepository fileTagsRepository;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Override
    public FileEntity convert(FileDTO fileDTO) {
        FileEntity newFileEntity = new FileEntity();
        newFileEntity.setFilename(fileDTO.getFilename());
        newFileEntity.setPath(fileDTO.getPath());

        FileType fileType = fileTypeRepository.getFileTypeByType(fileDTO.getType());
        newFileEntity.setType(fileType);

        Set<FileTag> tags = new HashSet<>();
        for (String tagName : fileDTO.getTags()) {
            FileTag tag = fileTagsRepository.findByTag(tagName);
            if (tag == null) {
                tag = new FileTag();
                tag.setTag(tagName);
                tag.setFileEntities(new HashSet<>());
                fileTagsRepository.save(tag);
            }
            tag.getFileEntities().add(newFileEntity);
            tags.add(tag);
        }

        newFileEntity.setTags(tags);

        Map<String, String> stringMetadata = fileDTO.getMetadata().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().toString()
                ));
        Metadata metadata = new Metadata(null, stringMetadata, newFileEntity);

        newFileEntity.setMetadata(metadata);

        System.out.println("FileEntity Converted successfully");
        return newFileEntity;
    }
}
