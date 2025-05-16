package com.example.localsearchengine.DTOs.MetadataDTOS;

import java.util.Map;

public class MetadataDTO {

    Map<String,Object> metadata;

    public MetadataDTO(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public MetadataDTO() {}


}
