package com.example.indexinglogger.Executors.FileProcessors;

import com.example.indexinglogger.DTOs.FileContentDTO;
import com.example.indexinglogger.DTOs.FileDTO;
import com.example.indexinglogger.DTOs.FileFullContents;
import com.example.indexinglogger.DTOs.FileTypeDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import java.util.*;
import java.util.stream.Collectors;

public class FileEntitiesProcessor {

    public List<FileFullContents> process(String rootDir, List<FileTypeDTO> types) throws IOException {

        Set<String> allowedExtensions = types.stream()
                .map(FileTypeDTO::getType)
                .collect(Collectors.toSet());

        List<FileFullContents> files = new ArrayList<>();

        try (var stream = Files.walk(Paths.get(rootDir))) {
            stream.filter(Files::isRegularFile).forEach(path -> {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    String fileExtension = getFileExtension(path, allowedExtensions);
                    String fileContent = Files.readString(path);

                    String filename = path.getFileName().toString();
                    String nameWithoutExtension = filename.contains(".") ? filename.substring(0, filename.indexOf(".")) : filename;
                    String fullPath = path.toAbsolutePath().toString();
                    String directoryPath = fullPath.replace(File.separator + filename, "");

                    FileContentDTO fileContentDTO = new FileContentDTO();
                    fileContentDTO.setContent(fileContent);
                    fileContentDTO.setFilename(nameWithoutExtension);
                    fileContentDTO.setPath(directoryPath);

                    if (fileExtension != null) {
                        FileDTO fileDTO = new FileDTO(
                                nameWithoutExtension,
                                directoryPath,
                                fileExtension,
                                (int) attrs.size(),
                                fileExtension,
                                new Timestamp(attrs.creationTime().toMillis()),
                                new Timestamp(attrs.lastModifiedTime().toMillis()),
                                new Timestamp(attrs.lastAccessTime().toMillis()),
                                new ArrayList<>()
                        );
                        FileFullContents fileFullContents = new FileFullContents();
                        fileFullContents.setContents(fileContentDTO);
                        fileFullContents.setFile(fileDTO);
                        files.add(fileFullContents);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file " + path.getFileName().toString());
                }
            });
        }
        return files;
    }

    private String getFileExtension(Path path, Set<String> allowedExtensions) {
        String name = path.getFileName().toString();
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex == -1) return null;

        String extension = name.substring(lastIndex + 1).toLowerCase();
        return allowedExtensions.contains(extension) ? extension : null;
    }

}
