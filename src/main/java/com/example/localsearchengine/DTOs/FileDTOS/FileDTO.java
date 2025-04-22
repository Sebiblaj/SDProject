package com.example.localsearchengine.DTOs.FileDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private String filename;
    private String path;
    private String type;
    private List<String> tags;
    private Map<String,Object> metadata;

}
