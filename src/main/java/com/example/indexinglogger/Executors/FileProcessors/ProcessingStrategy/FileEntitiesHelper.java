package com.example.indexinglogger.Executors.FileProcessors.ProcessingStrategy;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.indexinglogger.DTOs.*;
import com.example.indexinglogger.Entities.FileProperties;
import com.example.indexinglogger.Executors.RankingFunctions.FileRanker;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileEntitiesHelper {

    @Autowired
    private FileRanker fileRanker;

    public FileFullContents processDefaultFile(Path path, int depth, List<FileTypeDTO> types, String fileExtension) throws IOException, NoSuchAlgorithmException {
        Map<String,Object> metadataMap = extractMetadata(path,depth,types);

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
                fileExtension,
                contents
        );


        TagPathNameDTO tagDTO = new TagPathNameDTO(
                directoryPath,
                nameWithoutExtension,
                new ArrayList<>()
        );

        return new FileFullContents(
                fileDTO,
                fileContentDTO,
                tagDTO
        );
    }

    public FileFullContents processImageFile(Path path, int depth, List<FileTypeDTO> types, String fileExtension) throws IOException, NoSuchAlgorithmException {

        Map<String, Object> metadataMap = extractMetadata(path, depth, types);

        String nameWithoutExtension = metadataMap.get("filename").toString();
        String directoryPath = metadataMap.get("filepath").toString();

        StringBuilder imageDetails = new StringBuilder();
        try (InputStream inputStream = Files.newInputStream(path)) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    imageDetails.append(tag.toString()).append("\n");
                }
            }
        } catch (Exception e) {
            imageDetails.append("Could not read image metadata: ").append(e.getMessage());
        }

        byte[] imageBytes = Files.readAllBytes(path);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        FileDTO fileDTO = new FileDTO(nameWithoutExtension, directoryPath, fileExtension, new ArrayList<>(), metadataMap);

        String combinedContent = imageDetails.append("\n--- BASE64 IMAGE CONTENT ---\n")
                .append(base64Image).toString();

        FileContentDTO fileContentDTO = new FileContentDTO(directoryPath, nameWithoutExtension,fileExtension, combinedContent);
        TagPathNameDTO tagDTO = new TagPathNameDTO(directoryPath, nameWithoutExtension, new ArrayList<>());

        return new FileFullContents(fileDTO, fileContentDTO, tagDTO);
    }

    public FileFullContents processPdfFile(Path path, int depth, List<FileTypeDTO> types, String fileExtension) throws IOException, NoSuchAlgorithmException {
        Map<String, Object> metadataMap = extractMetadata(path, depth, types);

        String nameWithoutExtension = metadataMap.get("filename").toString();
        String directoryPath = metadataMap.get("filepath").toString();
        String contents;

        try (PDDocument document = PDDocument.load(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            contents = stripper.getText(document);

        }

        FileDTO fileDTO = new FileDTO(nameWithoutExtension, directoryPath, fileExtension, new ArrayList<>(), metadataMap);
        FileContentDTO fileContentDTO = new FileContentDTO(directoryPath, nameWithoutExtension,fileExtension, contents);
        TagPathNameDTO tagDTO = new TagPathNameDTO(directoryPath, nameWithoutExtension, new ArrayList<>());

        return new FileFullContents(fileDTO, fileContentDTO, tagDTO);
    }

    public FileFullContents processDocxFile(Path path, int depth, List<FileTypeDTO> types, String fileExtension) throws IOException, NoSuchAlgorithmException {
        Map<String,Object> metadataMap = extractMetadata(path, depth, types);
        String nameWithoutExtension = metadataMap.get("filename").toString();
        String directoryPath = metadataMap.get("filepath").toString();
        String contents;

        if (fileExtension.equalsIgnoreCase("doc")) {
            try (InputStream fis = Files.newInputStream(path)) {
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                contents = extractor.getText();
            }
        } else {
            try (InputStream fis = Files.newInputStream(path);
                 XWPFDocument document = new XWPFDocument(fis);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                contents = extractor.getText();
            }
        }

        FileDTO fileDTO = new FileDTO(nameWithoutExtension, directoryPath, fileExtension, new ArrayList<>(), metadataMap);
        FileContentDTO fileContentDTO = new FileContentDTO(directoryPath, nameWithoutExtension,fileExtension, contents);
        TagPathNameDTO tagDTO = new TagPathNameDTO(directoryPath, nameWithoutExtension, new ArrayList<>());

        return new FileFullContents(fileDTO, fileContentDTO, tagDTO);
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