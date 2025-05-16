package com.example.localsearchengine.DTOs.FileDTOS;


public class FileTypeDTO {

    private String type;
    private double weight;

    public FileTypeDTO(String type, double weight) {
        this.type = type;
        this.weight = weight;
    }

    public FileTypeDTO() {}

    public String getType() {
        return type;
    }
    public double getWeight() {
        return weight;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
