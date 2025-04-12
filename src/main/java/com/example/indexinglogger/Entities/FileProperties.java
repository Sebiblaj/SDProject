package com.example.indexinglogger.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileProperties {

    private String filename;
    private String path;
    private String extension;
    private long fileSize;
    private Timestamp lastModified;
    private Timestamp lasAccessed;
    private Timestamp creationTime;
    private int depth;
    private boolean readable;
    private boolean writable;
    private boolean executable;
}
