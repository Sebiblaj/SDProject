package com.example.localsearchengine.DTOs.MetadataDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataPathNameDTO {
    private String path;
    private String filename;
    private String extension;
    private MetadataDTO metadataDTO;
}
