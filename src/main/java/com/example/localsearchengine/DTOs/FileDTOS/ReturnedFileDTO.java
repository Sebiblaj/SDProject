package com.example.localsearchengine.DTOs.FileDTOS;

public class ReturnedFileDTO {

    private String filename;
    private String path;
    private String extension;

    public ReturnedFileDTO() {
    }

    public ReturnedFileDTO(String filename, String path, String extension) {
        this.filename = filename;
        this.path = path;
        this.extension = extension;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public String getExtension() {
        return extension;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "ReturnedFileDTO{" +
                "filename='" + filename + '\'' +
                ", path='" + path + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }
}
