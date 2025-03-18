package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.FileSearchResult;
import com.example.localsearchengine.Entites.FileContents;
import com.example.localsearchengine.Services.FileContentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "contents")
public class FileContentsController {

    @Autowired
    private FileContentsService fileContentsService;

    @GetMapping(value = "getFileContents/{id}")
    public ResponseEntity<FileContents> getFileContentsById(@PathVariable String id) {
        FileContents fileContents = fileContentsService.getFileContents(id);
        return fileContents != null ? ResponseEntity.ok(fileContents) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getFileContents/{path}/{filename}")
    public ResponseEntity<FileContents> getFileByPathAndName(@PathVariable String path,@PathVariable String filename) {
        FileContents fileContents = fileContentsService.getFileContents(path,filename);
        return fileContents !=null ? ResponseEntity.ok(fileContents) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getPreview/{id}")
    public ResponseEntity<String> getPreviewById(@PathVariable String id){
        String preview = fileContentsService.getPreview(id);
        return preview !=null ? ResponseEntity.ok(preview) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getPreview/{path}/{filename}")
    public ResponseEntity<String> getPreviewById(@PathVariable String path,@PathVariable String filename){
        String preview = fileContentsService.getPreview(path,filename);
        return preview != null ? ResponseEntity.ok(preview) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "searchKeyword/{id}")
    public ResponseEntity<FileSearchResult> searchInFileById(@PathVariable String id, @RequestParam String keyword){
        FileSearchResult fileSearchResult = fileContentsService.searchInFileById(id,keyword);
        return fileSearchResult != null ? ResponseEntity.ok(fileSearchResult) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "searchKeyword/{path}/{filename}")
    public ResponseEntity<FileSearchResult> searchInFileByPathAndName(@PathVariable String path, @PathVariable String filename, @RequestParam String keyword){
        FileSearchResult fileSearchResult = fileContentsService.searchInFileByPathAndName(path,filename,keyword);
        return fileSearchResult != null ? ResponseEntity.ok(fileSearchResult) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "setFileContents/{id}")
    public ResponseEntity<FileContents> setFileContentsById(@PathVariable String id, @RequestBody String contents) {
        FileContents fileContents = fileContentsService.setFileContents(id,contents);
        return fileContents != null ? ResponseEntity.ok(fileContents) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "setFileContents/{path}/{filename}")
    public ResponseEntity<FileContents> setFileContentsById(@PathVariable String path,@PathVariable String filename, @RequestBody String contents) {
        FileContents fileContents = fileContentsService.setFileContents(path,filename,contents);
        return fileContents != null ? ResponseEntity.ok(fileContents) : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "deleteContentsForFile/{path}/{filename}")
    public ResponseEntity<String> deleteContentsForPathAndFilename(@PathVariable String path,@PathVariable String filename){
        fileContentsService.deleteFileContents(path, filename);
        return ResponseEntity.ok("File Contents Deleted");
    }

    @DeleteMapping(value = "deleteContentsForFile/{id}")
    public ResponseEntity<String> deleteContentsForPathAndFilename(@PathVariable String id){
        fileContentsService.deleteFileContents(id);
        return ResponseEntity.ok("File Content Deleted");
    }



}
