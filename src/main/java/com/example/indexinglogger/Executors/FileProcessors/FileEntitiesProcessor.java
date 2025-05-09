package com.example.indexinglogger.Executors.FileProcessors;

import com.example.indexinglogger.DTOs.*;
import com.example.indexinglogger.Executors.FileProcessors.ProcessingStrategy.FileProcessorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class FileEntitiesProcessor {

    @Autowired
    private List<FileProcessorStrategy> strategies;

    public List<FileFullContents> process(String rootDir, List<FileTypeDTO> types, List<ReturnedFileDTO> indexedFiles) throws IOException {
        Set<String> allowedExtensionsSet = types.stream().map(FileTypeDTO::getType).collect(Collectors.toSet());

        Set<String> indexedFileKeys = indexedFiles.stream()
                .map(file -> generateKey(file.getFilename(), file.getPath(), file.getExtension()))
                .collect(Collectors.toSet());

        System.out.println("The indexed files are: " + indexedFileKeys + "\n\n");

        List<FileFullContents> files = new ArrayList<>();
        AtomicInteger depth = new AtomicInteger(0);

        try (var stream = Files.walk(Paths.get(rootDir))) {
            stream.forEach(path -> {
                if (Files.isDirectory(path)) {
                    depth.getAndIncrement();
                } else {
                    try {
                        String extension = getFileExtension(path);
                        if (extension != null && allowedExtensionsSet.contains(extension)) {
                            String name = path.getFileName().toString();
                            String nameWithoutExt = name.contains(".") ? name.substring(0, name.lastIndexOf(".")) : name;
                            String fullPath = path.normalize().toAbsolutePath().toString();
                            fullPath = fullPath.substring(fullPath.indexOf("TESTDIR"),fullPath.lastIndexOf(File.separator));

                            String key = generateKey(nameWithoutExt, fullPath, extension);
                            if (indexedFileKeys.contains(key)) {
                                System.out.println("File already indexed: " + key + " - skipping...");
                                return;
                            }else{
                                System.out.println("Indexing key "+key);
                            };

                            for (FileProcessorStrategy strategy : strategies) {
                                if (strategy.supports(extension)) {
                                    files.add(strategy.process(path, depth.get(), types, extension));
                                    break;
                                }
                            }
                        }
                    } catch (IOException | NoSuchAlgorithmException e) {
                        System.err.println("Failed to process file: " + path.getFileName());
                    }
                }
            });
        }

        System.out.println("Indexed "+ files.size() + " files.");

        return files;
    }

    private String getFileExtension(Path path) {
        String name = path.getFileName().toString();
        int lastIndex = name.lastIndexOf(".");
        return lastIndex != -1 ? name.substring(lastIndex + 1).toLowerCase() : null;
    }

    private String generateKey(String name, String path, String extension) {
        return name + "|" + path + "|" + extension;
    }

}

