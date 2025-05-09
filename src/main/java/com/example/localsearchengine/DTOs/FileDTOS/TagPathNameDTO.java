package com.example.localsearchengine.DTOs.FileDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagPathNameDTO {
    private String path;
    private String filename;
    private String extension;
    private List<Tag> tags;
}
