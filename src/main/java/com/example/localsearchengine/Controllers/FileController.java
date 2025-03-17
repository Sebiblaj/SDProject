package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping(value = "getFiles")
    public ResponseEntity<List<File>> getFiles() {return ResponseEntity.ok(fileService.getFiles());}

    @GetMapping(value = "getFileById")
    public ResponseEntity<File> getFileById(@RequestParam("id") int fileId) {
        return fileService.getFileById(fileId) !=null ? ResponseEntity.ok(fileService.getFileById(fileId)) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addFile")
    public ResponseEntity<File> addFile(@RequestBody File file) { return ResponseEntity.ok(fileService.addFile(file));}

    @PostMapping(value = "addMultipleFiles")
    public ResponseEntity<String> addMultipleFiles(@RequestBody List<File> files) { return ResponseEntity.ok(fileService.addMultipleFiles(files));}

    @PatchMapping("/updateFile/{id}")
    public ResponseEntity<File> updateFilename(@PathVariable int id, @RequestBody Map<String, String> request) {
        File responseFile = fileService.updateFile(id, request);
        return fileService != null ? ResponseEntity.ok(responseFile) : ResponseEntity.notFound().build();
    }
}
