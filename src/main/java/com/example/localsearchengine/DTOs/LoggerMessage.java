package com.example.localsearchengine.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.List;

public class LoggerMessage {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Timestamp timestamp;
    private String user;
    private ActivityDetails activity;

    public LoggerMessage(Timestamp timestamp, String user, ActivityDetails activity) {
        this.timestamp = timestamp;
        this.user = user;
        this.activity = activity;
    }

    public LoggerMessage() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public ActivityDetails getActivity() {
        return activity;
    }
    public void setActivity(ActivityDetails activity) {
        this.activity = activity;
    }

    public static class ActivityDetails {
        private int fileCount;
        private List<String> filesAccessed;
        private String description;

        public ActivityDetails(int fileCount, List<String> filesAccessed, String description) {
            this.fileCount = fileCount;
            this.filesAccessed = filesAccessed;
            this.description = description;
        }

        public ActivityDetails() {
        }

        public int getFileCount() {
            return fileCount;
        }
        public void setFileCount(int fileCount) {
            this.fileCount = fileCount;
        }
        public List<String> getFilesAccessed() {
            return filesAccessed;
        }

        public void setFilesAccessed(List<String> filesAccessed) {
            this.filesAccessed = filesAccessed;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
    }
}
