package com.example.localsearchengine.DTOs.ContentDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileSearchResult {
    private String filename;
    private String path;
    private List<Integer> lineNumbers;
    private List<String> excerpts;
}

