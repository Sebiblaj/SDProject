package com.example.localsearchengine.DTOs.ContentDTOS;


public class FileContentDTO {

    private String path;
    private String filename;
    private String extension;
    private String content;

    public FileContentDTO(String path, String filename, String extension, String content) {
        this.path = path;
        this.filename = filename;
        this.extension = extension;
        this.content = content;
    }

    public FileContentDTO(){}

    public String getPath() {
        return path;
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return extension;
    }

    public String getContent() {
        return content;
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

    public void setContent(String content) {
        this.content = content;
    }
}
