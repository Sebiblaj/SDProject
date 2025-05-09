package com.example.localsearchengine.DTOs.ContentDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileContentDTO {

    private String path;
    private String filename;
    private String extension;
    private String content;

}
