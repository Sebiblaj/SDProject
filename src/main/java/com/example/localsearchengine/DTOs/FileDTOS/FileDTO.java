package com.example.localsearchengine.DTOs.FileDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private String filename;
    private String path;
    private String type;
    private int filesize;
    private String extension;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp accessedAt;
    private List<String> tags;
}
