package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.*;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileContents;
import com.example.localsearchengine.Persistence.FileContentsRepository;
import com.example.localsearchengine.Persistence.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileContentsService {

    @Autowired
    private FileContentsRepository fileContentsRepository;

    @Autowired
    private FileRepository fileRepository;

    public String getFileContents(String id) {
        return fileContentsRepository.findByFileId(id);
    }

    public String getFileContents(String path,String filename) { return fileContentsRepository.getFileContentsByPathAndFilename(path,filename).getContents(); }

    public String getPreview(String id) {
        return fileContentsRepository.findPreviewByFileId(id);
    }

    public String getPreview(String path,String filename) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path,filename);
        return fileContents != null ? fileContents.getPreview() : null;
    }

    public FileSearchResult searchInFileById(String id, KeywordDTO keyword) {
        String formattedKeyword = keyword.getKeyword().replace(" ", " & ");
        FileContents fileContents = fileContentsRepository.searchFilesByKeyword(formattedKeyword, Integer.valueOf(id));
        if(fileContents != null) {
            return searchInContents(fileContents.getFile().getFilename(),fileContents.getContents(),keyword.getKeyword());
        }
        return null;
    }

    public FileSearchResult searchInFileByPathAndName(String path, String filename, KeywordDTO keyword) {
        FileContents fileContents = fileContentsRepository.searchFilesByKeyword(path, filename, keyword.getKeyword());
        if(fileContents != null) {
            return searchInContents(fileContents.getFile().getFilename(),fileContents.getContents(),keyword.getKeyword());
        }
        return null;
    }

    public List<FileSearchResult> searchInFilesForKeyword(KeywordDTO keyword) {
        List<FileContents> fileContents = fileContentsRepository.searchFilesByKeyword(keyword.getKeyword());
        List<FileSearchResult> fileSearchResults = new ArrayList<>();
        if(!fileContents.isEmpty()) {
            for(FileContents fileContent: fileContents) {
                fileSearchResults.add(searchInContents(fileContent.getFile().getFilename(),fileContent.getContents(),keyword.getKeyword()));
            }
        }
        return fileSearchResults;
    }

    @Transactional
    public String setFileContents(String id, ContentsDTO contentsDTO) {
        FileContents fileContents = fileContentsRepository.findFileContentsByFileId(id);
        if(fileContents != null) {
            fileContents.setContents(contentsDTO.getContent());
            fileContentsRepository.save(fileContents);
            return "File Content Added Successfully";
        }else{
            File file=fileRepository.findById(id).orElse(null);
            if(file != null) {
                FileContents newFileContents = new FileContents();
                newFileContents.setFile(file);
                newFileContents.setContents(contentsDTO.getContent());
                fileContentsRepository.save(newFileContents);
                return "File Content Added Successfully";
            }
        }
        return "File Content Not Added";
    }

    @Transactional
    public String setFileContents(List<FileContentDTO> fileFullContents) {
        System.out.println("I have reached here");
        List<PathAndName> pathAndNames = fileFullContents.stream()
                .map(dto -> new PathAndName(dto.getPath(), dto.getFilename()))
                .toList();

        List<String> paths = fileFullContents.stream()
                .map(FileContentDTO::getPath)
                .toList();

        List<String> filenames = fileFullContents.stream()
                .map(FileContentDTO::getFilename)
                .toList();

        System.out.println("The paths are " + paths);
        System.out.println("The filenames are " + filenames);


        List<File> files =fileRepository.findFilesByPathAndFilename(paths,filenames);

        System.out.println("here");

        if (files.isEmpty()) {
            return "Files Not Found";
        }

        System.out.println("and here");

        List<FileContents> fileContentsList = new ArrayList<>();

        for (FileContentDTO fileDTO : fileFullContents) {
            File matchingFile = files.stream()
                    .filter(f -> f.getFilename().trim().equals(fileDTO.getFilename().trim())
                            && f.getPath().trim().equals(fileDTO.getPath().trim()))
                    .findFirst()
                    .orElse(null);


            if (matchingFile != null) {
                FileContents fileContents = fileContentsRepository.findByFile(matchingFile);
                if(fileContents != null) {
                    fileContents.setFile(matchingFile);
                    fileContents.setContents(fileDTO.getContent());
                    fileContentsList.add(fileContents);
                }else{
                    FileContents newFileContents = new FileContents();
                    newFileContents.setFile(matchingFile);
                    newFileContents.setContents(fileDTO.getContent());
                    fileContentsList.add(newFileContents);
                }

            }else{
                System.out.println("File Not Found");
            }
        }
        System.out.println("saving");
        fileContentsRepository.saveAll(fileContentsList);
        System.out.println("File Content Added Successfully");
        return "Contents Added Successfully";

    }

    @Transactional
    public String setFileContents(String path,String filename, ContentsDTO contentsDTO) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path,filename);
        if(fileContents != null) {
            fileContents.setContents(contentsDTO.getContent());
            fileContentsRepository.save(fileContents);
            return "File Content Added Successfully";
        }else{
            File file=fileRepository.getFileByPathAndFilename(path,filename);
            if(file != null) {
                FileContents newFileContents = new FileContents();
                newFileContents.setFile(file);
                newFileContents.setContents(contentsDTO.getContent());
                fileContentsRepository.save(newFileContents);
                return "File Content Added Successfully";
            }
        }
        return "File Content Not Added";
    }

    @Transactional
    public void deleteFileContents(String id) {fileContentsRepository.deleteById(id);}

    @Transactional
    public void deleteFileContents(String path,String filename){
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path,filename);
        if(fileContents != null) {
            fileContentsRepository.delete(fileContents);
        }
    }

    private FileSearchResult searchInContents(String filename, String contents, String keyword) {
        List<Integer> lineNumbers = new ArrayList<>();
        List<String> excerpts = new ArrayList<>();
        String[] lines = contents.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(keyword)) {
                lineNumbers.add(i + 1);
                excerpts.add(lines[i].trim());
            }
        }

        if (lineNumbers.isEmpty()) {
            return null;
        }

        return new FileSearchResult(filename, lineNumbers, excerpts);
    }

}
