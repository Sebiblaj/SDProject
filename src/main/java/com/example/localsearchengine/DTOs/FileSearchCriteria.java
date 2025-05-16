package com.example.localsearchengine.DTOs;

import java.util.List;

public class FileSearchCriteria {

    private List<String> names;
    private List<String> paths;
    private List<String> keywords;

    public FileSearchCriteria(List<String> names, List<String> paths, List<String> keywords) {
        this.names = names;
        this.paths = paths;
        this.keywords = keywords;
    }

    public FileSearchCriteria() {}

    public List<String> getNames() {
        return names;
    }
    public void setNames(List<String> names) {
        this.names = names;
    }
    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "FileSearchCriteria{" +
                "names=" + names +
                ", paths=" + paths +
                ", keywords=" + keywords +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSearchCriteria that = (FileSearchCriteria) o;
        return names.equals(that.names) && paths.equals(that.paths) && keywords.equals(that.keywords);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
