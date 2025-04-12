package com.example.indexinglogger.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataPathNameDTO {
    private String path;
    private String filename;
    private MetadataDTO metadataDTO;
}
