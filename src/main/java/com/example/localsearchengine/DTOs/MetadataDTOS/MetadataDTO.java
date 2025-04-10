package com.example.localsearchengine.DTOs.MetadataDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataDTO {

    Map<String,String> metadata;
}
