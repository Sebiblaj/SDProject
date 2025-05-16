package com.example.localsearchengine.DTOs.FileDTOS;

import java.util.List;
import java.util.Map;

public class FileDTO {

    private String filename;
    private String path;
    private String type;
    private List<String> tags;
    private Map<String,Object> metadata;

    public FileDTO(String filename, String path, String type, List<String> tags, Map<String, Object> metadata) {
        this.filename = filename;
        this.path = path;
        this.type = type;
        this.tags = tags;
        this.metadata = metadata;
    }

    public FileDTO(){}

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
