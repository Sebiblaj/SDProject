package com.example.localsearchengine.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileContentDTO {

    private String path;
    private String content;
    private String filename;
}
