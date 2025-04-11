package com.example.localsearchengine.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggerMessage {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Timestamp timestamp;
    private String user;
    private ActivityDetails activity;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityDetails {
        private int fileCount;
        private List<String> filesAccessed;
        private String description;
    }
}
