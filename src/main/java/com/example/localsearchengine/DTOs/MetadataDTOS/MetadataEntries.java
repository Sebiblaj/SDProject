package com.example.localsearchengine.DTOs.MetadataDTOS;


public class MetadataEntries {

    private String key;
    private String value;

    public MetadataEntries(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public MetadataEntries() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
