package com.example.indexinglogger.DTOs;

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
    private List<Tag> tags;
}
