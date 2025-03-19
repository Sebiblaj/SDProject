package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.ContentsDTO;
import com.example.localsearchengine.DTOs.FileSearchResult;
import com.example.localsearchengine.DTOs.KeywordDTO;
import com.example.localsearchengine.Entites.FileContents;
import com.example.localsearchengine.Persistence.FileContentsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileContentsService {

    @Autowired
    private FileContentsRepository fileContentsRepository;

    public FileContents getFileContents(String id) { return fileContentsRepository.findById(id).orElse(null); }

    public FileContents getFileContents(String path,String filename) { return fileContentsRepository.getFileContentsByPathAndFilename(path,filename); }

    public String getPreview(String id) {
        FileContents fileContents = fileContentsRepository.findById(id).orElse(null);
        return fileContents != null ? fileContents.getPreview() : null;
    }

    public String getPreview(String path,String filename) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path,filename);
        return fileContents != null ? fileContents.getPreview() : null;
    }

    public FileSearchResult searchInFileById(String id, KeywordDTO keyword) {
        FileContents fileContents = fileContentsRepository.searchFilesByKeyword(keyword.getKeyword(), Integer.valueOf(id));
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
    public FileContents setFileContents(String id, ContentsDTO contentsDTO) {
        FileContents fileContents = fileContentsRepository.findById(id).orElse(null);
        if(fileContents != null) {
            fileContents.setContents(contentsDTO.getContent());
            return  fileContentsRepository.save(fileContents);
        }
        return null;
    }

    @Transactional
    public FileContents setFileContents(String path,String filename, ContentsDTO contentsDTO) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path,filename);
        if(fileContents != null) {
            fileContents.setContents(contentsDTO.getContent());
            return  fileContentsRepository.save(fileContents);
        }
        return null;
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
