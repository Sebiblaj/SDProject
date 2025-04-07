package org.example.queryworker;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceExecutors {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<FileDTO> findFilesByFileName(File dir, String fileName) {
        File[] files = dir.listFiles();
        List<FileDTO> fileDTOs = new ArrayList<>();

        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileDTOs.addAll(findFilesByFileName(file, fileName));
                } else {
                    if (fileName.equals(file.getName())) {
                        try {
                            Path filePath = file.toPath();
                            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                            Instant lastModifiedInstant = attrs.lastModifiedTime().toInstant();
                            Date lastModified = Date.from(lastModifiedInstant);
                            long size = attrs.size();
                            boolean isDirectory = file.isDirectory();
                            String extension = isDirectory ? "folder" : getFileExtension(file.getName());
                            String preview ;
                            try {
                                String contents = Files.readString(filePath);
                                String sanitizedContents = contents.replaceAll("[\\x00-\\x1F\\x7F]", "");
                                preview = sanitizedContents.length() > 100 ? sanitizedContents.substring(0, 100) : sanitizedContents;
                                if (sanitizedContents.length() > 100) {
                                    preview += "...";
                                }
                            } catch (IOException e) {
                                preview = "Not available";
                            }

                            FileDTO fileDTOInfo = new FileDTO(
                                    file.getAbsolutePath(),
                                    file.getName(),
                                    size,
                                    lastModified,
                                    extension,
                                    isDirectory,
                                    preview
                            );

                            fileDTOs.add(fileDTOInfo);
                        } catch (Exception e) {
                            System.err.println("Error processing file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        return fileDTOs;
    }


    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1);
    }
}
