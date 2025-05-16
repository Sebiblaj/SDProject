package com.example.localsearchengine.DTOs.MetadataDTOS;


public class MetadataPathNameDTO {
    private String path;
    private String filename;
    private String extension;
    private MetadataDTO metadataDTO;

    public MetadataPathNameDTO(String path, String filename, String extension, MetadataDTO metadataDTO) {
        this.path = path;
        this.filename = filename;
        this.extension = extension;
        this.metadataDTO = metadataDTO;
    }

    public MetadataPathNameDTO(){}

    public String getPath() {
        return path;
    }
    public String getFilename() {
        return filename;
    }
    public String getExtension() {
        return extension;
    }
    public MetadataDTO getMetadataDTO() {
        return metadataDTO;
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

    public void setMetadataDTO(MetadataDTO metadataDTO) {
        this.metadataDTO = metadataDTO;
    }
}
