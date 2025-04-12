package com.example.localsearchengine.DTOs.FileDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnedFileDTO {

    private String filename;
    private String path;
}

