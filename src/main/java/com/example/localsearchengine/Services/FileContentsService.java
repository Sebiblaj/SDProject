package com.example.localsearchengine.Services;

import com.example.localsearchengine.DTOs.FileSearchResult;
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

    public FileSearchResult searchInFileById(String id, String keyword) {
        Optional<FileContents> fileContents = fileContentsRepository.findById(id);
        if(fileContents.isPresent()) {
            FileContents fileContents1 = fileContents.get();
            return searchInContents(fileContents1.getFile().getFilename(),fileContents1.getContents(),keyword);
        }
        return null;
    }

    public FileSearchResult searchInFileByPathAndName(String path, String filename, String keyword) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path, filename);
        if(fileContents != null) {
            return searchInContents(fileContents.getFile().getFilename(),fileContents.getContents(),keyword);
        }
        return null;
    }

    @Transactional
    public FileContents setFileContents(String id, String contents) {
        FileContents fileContents = fileContentsRepository.findById(id).orElse(null);
        if(fileContents != null) {
            fileContents.setContents(contents);
            return  fileContentsRepository.save(fileContents);
        }
        return null;
    }

    @Transactional
    public FileContents setFileContents(String path,String filename, String contents) {
        FileContents fileContents = fileContentsRepository.getFileContentsByPathAndFilename(path,filename);
        if(fileContents != null) {
            fileContents.setContents(contents);
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
