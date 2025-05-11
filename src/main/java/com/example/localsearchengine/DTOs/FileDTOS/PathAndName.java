package com.example.localsearchengine.DTOs.FileDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathAndName {

    private String path;
    private String name;
    private String extension;
}
