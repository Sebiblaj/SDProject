package org.example.queryworker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDTO {
    private String fullPath;
    private String name;
    private long size;

    private String lastModified;

    private String extension;
    private boolean isDirectory;
    private String preview;

    public FileDTO(String fullPath, String name, long size, Date lastModified, String extension, boolean isDirectory, String preview) {
        this.fullPath = fullPath;
        this.name = name;
        this.size = size;
        this.lastModified = formatDate(lastModified);
        this.extension = extension;
        this.isDirectory = isDirectory;
        this.preview = preview;
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // You can change the format here
        return sdf.format(date);
    }

    // Getters and Setters
    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "fullPath='" + fullPath + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", lastModified=" + lastModified +
                ", extension='" + extension + '\'' +
                ", isDirectory=" + isDirectory +
                ", preview='" + preview + '\'' +
                '}';
    }
}
