package com.example.indexinglogger.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataDTO {
    private Map<String,Object> metadata;
}
