package com.example.localsearchengine.DTOs.FileDTOS;


public class PathAndName {

    private String path;
    private String name;
    private String extension;

    public PathAndName(String path, String name, String extension) {
        this.path = path;
        this.name = name;
        this.extension = extension;
    }

    public PathAndName(){}

    public String getPath() {
        return path;
    }
    public String getName() {
        return name;
    }
    public String getExtension() {
        return extension;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
}
