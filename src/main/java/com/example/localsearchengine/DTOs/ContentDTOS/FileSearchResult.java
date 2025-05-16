package com.example.localsearchengine.DTOs.ContentDTOS;


import java.util.List;

public class FileSearchResult {
    private String filename;
    private String path;
    private List<Integer> lineNumbers;
    private List<String> excerpts;

    public FileSearchResult(String filename, String path, List<Integer> lineNumbers, List<String> excerpts) {
        this.filename = filename;
        this.path = path;
        this.lineNumbers = lineNumbers;
        this.excerpts = excerpts;
    }

    public String getFilename() {
        return filename;
    }
    public String getPath() {
        return path;
    }
    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }

    public List<String> getExcerpts() {
        return excerpts;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setLineNumbers(List<Integer> lineNumbers) {
        this.lineNumbers = lineNumbers;
    }

    public void setExcerpts(List<String> excerpts) {
        this.excerpts = excerpts;
    }


}
