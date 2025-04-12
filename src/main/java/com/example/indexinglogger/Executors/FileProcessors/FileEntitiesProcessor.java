package com.example.indexinglogger.Executors.FileProcessors;

import com.example.indexinglogger.DTOs.*;
import com.example.indexinglogger.Entities.FileProperties;
import com.example.indexinglogger.Executors.RankingFunctions.FileRanker;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FileEntitiesProcessor {

    @Autowired
    private FileRanker fileRanker;

    public List<FileFullContents> process(String rootDir, List<FileTypeDTO> types) throws IOException{

        Set<String> allowedExtensions = types.stream()
                .map(FileTypeDTO::getType)
                .collect(Collectors.toSet());

        List<FileFullContents> files = new ArrayList<>();

        AtomicInteger depth = new AtomicInteger(0);

        try (var stream = Files.walk(Paths.get(rootDir))) {
            stream.forEach(path -> {
                if(Files.isDirectory(path)) {
                    depth.getAndIncrement();
                }else {
                    try {
                        String fileExtension = getFileExtension(path,allowedExtensions);
                        if (fileExtension != null) {

                            Map<String,Object> metadataMap = extractMetadata(path,depth.get(),types);

                            String nameWithoutExtension = metadataMap.get("filename").toString();
                            String directoryPath = metadataMap.get("filepath").toString();
                            String contents = String.join("\n", Files.readAllLines(path));

                            FileDTO fileDTO = new FileDTO(
                                    nameWithoutExtension,
                                    directoryPath,
                                    fileExtension,
                                    new ArrayList<>(),
                                    metadataMap
                            );

                            FileContentDTO fileContentDTO = new FileContentDTO(
                                    directoryPath,
                                    nameWithoutExtension,
                                    contents
                            );


                            TagPathNameDTO tagDTO = new TagPathNameDTO(
                                    directoryPath,
                                    nameWithoutExtension,
                                    new ArrayList<>()
                            );

                            FileFullContents fileFullContents = new FileFullContents(
                                    fileDTO,
                                    fileContentDTO,
                                    tagDTO
                            );

                            files.add(fileFullContents);
                        }
                    } catch (IOException | NoSuchAlgorithmException e) {
                        System.err.println("Error reading file " + path.getFileName().toString());
                    }
                }
            });
        }
        return files;
    }

    private String getFileExtension(Path path, Set<String> allowedExtensions) {
        String name = path.getFileName().toString();
        int lastIndex = name.lastIndexOf(".");

        if (lastIndex == -1) {
            return null;
        }

        String extension = name.substring(lastIndex + 1).toLowerCase();
        return allowedExtensions.contains(extension) ? extension : null;
    }

    private Map<String,Object> extractMetadata(Path path,int depth,List<FileTypeDTO> allowedExtensions) throws IOException, NoSuchAlgorithmException {

        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        String fileExtension = getFileExtension(path, allowedExtensions.stream().map(FileTypeDTO::getType).collect(Collectors.toSet()));
        String filename = path.getFileName().toString();
        String nameWithoutExtension = filename.contains(".") ? filename.substring(0, filename.indexOf(".")) : filename;
        String fullPath = path.toAbsolutePath().toString();
        String dirPath = fullPath.substring(0, fullPath.lastIndexOf(File.separator));
        String filepath = dirPath.substring(fullPath.indexOf("TESTDIR"));
        long fileSize = attrs.size();
        Timestamp creationTime = new Timestamp(attrs.creationTime().toMillis());
        Timestamp lastModifiedTime = new Timestamp(attrs.lastModifiedTime().toMillis());
        Timestamp lastAccessTime = new Timestamp(attrs.lastAccessTime().toMillis());

        boolean isReadable = Files.isReadable(path);
        boolean isWritable = Files.isWritable(path);
        boolean isExecutable = Files.isExecutable(path);

        FileProperties fileProperties = new FileProperties(
                filename,
                filepath,
                fileExtension,
                fileSize,
                lastModifiedTime,
                lastAccessTime,
                creationTime,
                depth,
                isReadable,
                isWritable,
                isExecutable
        );

        float weight = fileRanker.rankingFunction(fileProperties,allowedExtensions);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(Files.readAllBytes(path));
        String fileHash = new BigInteger(1, hashBytes).toString(16);

        String mimeType = Files.probeContentType(path);


        Map<String,Object> metadata = new HashMap<>();
        metadata.put("fullpath", fullPath);
        metadata.put("filepath",filepath);
        metadata.put("filesize",fileSize);
        metadata.put("lastmodified",lastModifiedTime);
        metadata.put("lastaccess",lastAccessTime);
        metadata.put("creationtime",creationTime);
        metadata.put("weight",weight);
        metadata.put("depth",depth);
        metadata.put("extension",fileExtension);
        metadata.put("filename",nameWithoutExtension);
        metadata.put("filehash", fileHash);
        metadata.put("readable", isReadable);
        metadata.put("writable", isWritable);
        metadata.put("executable", isExecutable);
        metadata.put("mimetype",mimeType);

        return metadata;
    }
}
