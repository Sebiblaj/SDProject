package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.ContentDTOS.ContentsDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileContentDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileSearchResult;
import com.example.localsearchengine.DTOs.LoggerMessage;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileContents;
import com.example.localsearchengine.Persistence.FileContentsRepository;
import com.example.localsearchengine.Persistence.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileContentsService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private FileContentsRepository fileContentsRepository;

    @Autowired
    private FileRepository fileRepository;

    @Value("${kafka.topic.logs}")
    private String TOPIC;

    @Autowired
    public void KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Object message) {
        kafkaTemplate.send(TOPIC, message);
    }

    public String getFileContents(String path, String filename) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path, filename);

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has accessed the contents for the file."
                )
        ));

        return fileContents != null ? fileContents.getContents() : null;
    }

    public String getPreview(String path, String filename) {

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has accessed the contents preview for the file."
                )
        ));
        return fileContentsRepository.getFileContentsByPathAndFilename(path, filename).getPreview();
    }

    public List<FileSearchResult> searchInFileByPathAndName(List<String> paths, List<String> filenames, List<String> keywords) {
        List<FileSearchResult> results = new ArrayList<>();
        boolean success = true;

        System.out.println("The paths are " + paths);
        System.out.println("The filenames are " + filenames);
        System.out.println("The keywords are " + keywords);

        List<String> searchPaths = (paths == null || paths.isEmpty()) ? null : paths;
        List<String> searchFilenames = (filenames == null || filenames.isEmpty()) ? null : filenames;

        if (keywords == null || keywords.isEmpty()) {
            success = false;
        } else {
            String tsQueryString = String.join(" & ", keywords);
            List<FileContents> matchedFiles = new ArrayList<>();

            if (searchPaths == null && searchFilenames == null) {
                matchedFiles = fileContentsRepository.searchFileByKeyword(tsQueryString);
            } else if (searchPaths != null && searchFilenames == null) {
                for (String path : searchPaths) {
                    matchedFiles.addAll(fileContentsRepository.getFileContentsByPath(path, tsQueryString));
                }
            } else if (searchPaths == null) {
                for (String filename : searchFilenames) {
                    matchedFiles.addAll(fileContentsRepository.getFileContentsByName(filename, tsQueryString));
                }
            } else {
                for (String path : searchPaths) {
                    for (String filename : searchFilenames) {
                        matchedFiles.add(fileContentsRepository.searchFileByKeyword(path, filename, tsQueryString));
                    }
                }
            }

            if (matchedFiles.isEmpty()) {
                success = false;
            } else {
                for (FileContents fileContent : matchedFiles) {
                    System.out.println("Checking " + fileContent);
                    String finalFilename = fileContent.getFile().getFilename();
                    String finalPath = fileContent.getFile().getPath();

                    List<Integer> lineNumbers = new ArrayList<>();
                    List<String> excerpts = new ArrayList<>();

                    List<String> lines = fileContent.getContents().lines().toList();
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        for (String keyword : keywords) {
                            if (line.contains(keyword)) {
                                lineNumbers.add(i + 1);
                                excerpts.add(line);
                            }
                        }
                    }

                    if (!lineNumbers.isEmpty()) {
                        results.add(new FileSearchResult(finalFilename, finalPath, lineNumbers, excerpts));
                    }
                }
            }
        }

        String description;
        if (success) {
            description = "User has accessed the file(s) to search in contents for keyword(s) '" +
                    String.join(", ", keywords) + "'.";
        } else {
            description = "User has tried to access file(s) to search in contents but failed";
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        List.of(
                                (searchPaths != null ? String.join(",", searchPaths) : "*") + "/" +
                                        (searchFilenames != null ? String.join(",", searchFilenames) : "*")
                        ),
                        description
                )
        ));

        return results.isEmpty() ? null : results;
    }

    public List<FileSearchResult> searchInFilesForKeyword(String keyword) {
        List<FileContents> fileContentsList = fileContentsRepository.searchFileByKeyword(keyword);
        List<FileSearchResult> fileSearchResults = new ArrayList<>();
        if (!fileContentsList.isEmpty()) {
            for (FileContents fileContent : fileContentsList) {
                fileSearchResults.add(searchInContents(fileContent.getFile().getFilename(),
                        fileContent.getFile().getPath(), fileContent.getContents(), keyword));
            }
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        fileSearchResults.size(),
                        fileSearchResults.stream()
                                .map(f-> f.getPath() + "/" + f.getFilename()).toList(),
                        "User has accessed the contents of the files for the keyword: " + keyword + "."
                )
        ));

        return fileSearchResults;
    }

    @Transactional
    public String setFileContents(List<FileContentDTO> fileFullContents) {
        Map<String, File> fileMap = fileRepository.findFilesByPathAndFilename(
                fileFullContents.stream().map(FileContentDTO::getPath).collect(Collectors.toList()),
                fileFullContents.stream().map(FileContentDTO::getFilename).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(file -> file.getPath() + file.getFilename(), file -> file));

        List<FileContents> fileContentsList = new ArrayList<>();
        for (FileContentDTO fileDTO : fileFullContents) {
            File file = fileMap.get(fileDTO.getPath() + fileDTO.getFilename());
            if (file != null) {
                FileContents fileContents = fileContentsRepository.findByFile(file);
                if (fileContents != null) {
                    fileContents.setContents(fileDTO.getContent());
                } else {
                    fileContents = new FileContents();
                    fileContents.setFile(file);
                    fileContents.setContents(fileDTO.getContent());
                }
                String preview = fileDTO.getContent().length() > 100 ? fileDTO.getContent().substring(0, 100) + "..." : fileDTO.getContent();
                fileContents.setPreview(preview);
                fileContentsList.add(fileContents);
            }
        }

        fileContentsRepository.saveAll(fileContentsList);

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        fileContentsList.size(),
                        fileContentsList.stream()
                                .map(f-> f.getFile().getPath() + "/" + f.getFile().getFilename()).toList(),
                        "User has set the contents for the files."
                )
        ));

        return "Contents Added Successfully";
    }

    @Transactional
    public String setFileContents(String path, String filename, ContentsDTO contentsDTO) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path, filename);
        String description;
        boolean success = true;
        if (fileContents != null) {
            fileContents.setContents(contentsDTO.getContent());
            fileContentsRepository.save(fileContents);
        } else {
            File file = fileRepository.getFileByPathAndFilename(path, filename);
            if (file != null) {
                FileContents newFileContents = new FileContents();
                newFileContents.setFile(file);
                newFileContents.setContents(contentsDTO.getContent());
                fileContentsRepository.save(newFileContents);
            }else{
                success = false;
            }
        }

        if (success) {
            description = "User has set contents to the file.";
        }else{
            description = "User has tried to set contents to the file but the file does not exist.";
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        0,
                        new ArrayList<>(),
                        description
                )
        ));
        return success?"Success":"Failed";
    }

    @Transactional
    public void deleteFileContents(String path, String filename) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path, filename);
        if (fileContents != null) {
            fileContentsRepository.delete(fileContents);
        }

        sendMessage(new LoggerMessage(
                Timestamp.valueOf(LocalDateTime.now()),
                "sebir",
                new LoggerMessage.ActivityDetails(
                        1,
                        new ArrayList<>(List.of(path + "/" + filename)),
                        "User has deleted the contents of the file."
                )
        ));
    }

    private FileSearchResult searchInContents(String filename,String path, String contents, String keyword) {
        List<Integer> lineNumbers = new ArrayList<>();
        List<String> excerpts = new ArrayList<>();
        String[] lines = contents.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(keyword)) {
                lineNumbers.add(i + 1);
                excerpts.add(lines[i].trim());
            }
        }

        return lineNumbers.isEmpty() ? null : new FileSearchResult(filename,path, lineNumbers, excerpts);
    }


}
