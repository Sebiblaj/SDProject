package org.example.querymanager.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private String fullPath;
    private String name;
    private long size;
    private String lastModified;
    private String extension;
    private boolean isDirectory;
    private String preview;
}
