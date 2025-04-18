package com.example.localsearchengine.ServiceExecutors.Convertors;

import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataDTO;
import com.example.localsearchengine.Entites.Metadata;

import java.util.HashMap;
import java.util.Map;

public class MetadataConvertor implements Convertor<Metadata, MetadataDTO> {

    @Override
    public MetadataDTO convert(Metadata payload) {
        Map<String, Object> metadata = new HashMap<>(payload.getValues());
        return new MetadataDTO(metadata);
    }
}
