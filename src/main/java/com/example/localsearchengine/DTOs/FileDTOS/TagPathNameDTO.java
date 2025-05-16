package com.example.localsearchengine.DTOs.FileDTOS;

import java.util.List;


public class TagPathNameDTO {
    private String path;
    private String filename;
    private String extension;
    private List<Tag> tags;

    public TagPathNameDTO(String path, String filename, String extension, List<Tag> tags) {
        this.path = path;
        this.filename = filename;
        this.extension = extension;
        this.tags = tags;
    }

    public TagPathNameDTO(){}

    public String getPath() {
        return path;
    }
    public String getFilename() {
        return filename;
    }
    public String getExtension() {
        return extension;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
