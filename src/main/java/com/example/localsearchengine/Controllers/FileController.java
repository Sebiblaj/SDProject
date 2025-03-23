package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.*;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "file")
public class FileController {

    /*
    This contains APIs for managing the files in different manners
     */

    @Autowired
    private FileService fileService;

    @GetMapping(value = "findAllFiles")
    public ResponseEntity<List<File>> getFiles() {
        List<File> files = fileService.getFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping(value = "getTypeForFile/{id}")
    public ResponseEntity<FileType> getTypeForFile(@PathVariable String id) {
        FileType fileType = fileService.getFileTypeByFileId(id);
        return fileType != null ? ResponseEntity.ok(fileType) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getTypeForFile/{path}/{filename}")
    public ResponseEntity<FileType> getTypeForFile(@PathVariable String path, @PathVariable String filename) {
        FileType fileType = fileService.getFileTypeByFilePath(path, filename);
        return fileType != null ? ResponseEntity.ok(fileType) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "findFile/{id}")
    public ResponseEntity<File> getFileById(@PathVariable String id) {
        File file = fileService.getFileById(id);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "findFile/{path}/{filename}")
    public ResponseEntity<File> getFileById(@PathVariable String path, @PathVariable String filename) {
        File file = fileService.getFileById(path,filename);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addFile")
    public ResponseEntity<String> addFile(@RequestBody FileDTO file) {
        File savedFile = fileService.addFile(file);
        return savedFile != null ? ResponseEntity.ok("File added successfully") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(value = "addMultipleFiles")
    public ResponseEntity<String> addMultipleFiles(@RequestBody List<FileDTO> files) {
        String response = fileService.addMultipleFiles(files);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.ok("Could not add files");
    }

    @PutMapping("updateFile/{id}")
    public ResponseEntity<File> updateFilename(@PathVariable String id, @RequestBody List<MetadataEntries> request) {
        File responseFile = fileService.updateFile(id, request);
        return responseFile != null ? ResponseEntity.ok(responseFile) : ResponseEntity.notFound().build();
    }

    @PutMapping("updateFile/{path}/{filename}")
    public ResponseEntity<File> updateFilename(@PathVariable String path,@PathVariable String filename, @RequestBody List<MetadataEntries> request) {
        File responseFile = fileService.updateFile(path,filename, request);
        return responseFile != null ? ResponseEntity.ok(responseFile) : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "deleteFile/{id}")
    public ResponseEntity<String> deleteFileById(@PathVariable String id) {
        String result = fileService.deleteFile(id);
        return result.equals("File deleted") ? ResponseEntity.ok("File deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "deleteFile/{path}/{filename}")
    public ResponseEntity<String> deleteFileById(@PathVariable String path, @PathVariable String filename) {
        String result = fileService.deleteFile(path, filename);
        return result.equals("File deleted") ? ResponseEntity.ok("File deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/deleteMultipleFilesById")
    public ResponseEntity<String> deleteMultipleFiles(@RequestBody List<FileIdDTO> fileIds) {
        boolean allDeleted = fileService.deleteMultipleFiles(fileIds);
        return allDeleted ? ResponseEntity.ok("All files deleted successfully") : ResponseEntity.status(500).body("Some files could not be deleted");
    }

    @DeleteMapping(value = "/deleteMultipleFilesByPathAndName")
    public ResponseEntity<String> deleteMultipleFilesByPathAndName(@RequestBody List<PathAndName> fileIds) {
        boolean result = fileService.deleteByPathAndFilename(fileIds);
        return result ? ResponseEntity.ok("All files deleted successfully") : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "findBySizeInterval/{min}/{max}")
    public ResponseEntity<List<File>> findBySizeInterval(@PathVariable int min, @PathVariable int max) {
        List<File> files = fileService.findBySizeInterval(min, max);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }

    @GetMapping(value = "/findByFilename/{filename}")
    public ResponseEntity<List<File>> searchFiles(@PathVariable String filename) {
        List<File> files = fileService.searchFilesByName(filename);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }

    @GetMapping(value = "findByExtension/{ext}")
    public ResponseEntity<List<File>> getFilesByExtension(@PathVariable String ext) {
        List<File> files = fileService.searchFilesByExtension(ext);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }

/*
The next part is for the Tags of the file
*/

    @GetMapping(value = "getTagsForFile/{id}")
    public ResponseEntity<List<Tag>> getTagsForFile(@PathVariable String id) {
        List<Tag> tags = fileService.getTagsForFile(id);
        return tags != null ? ResponseEntity.ok(tags) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getTagsForFile/{path}/{filename}")
    public ResponseEntity<List<Tag>> getTagsForFile(@PathVariable String path, @PathVariable String filename) {
        List<Tag> tags = fileService.getTagsForFile(path, filename);
        return tags != null ? ResponseEntity.ok(tags) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getAllTags")
    public ResponseEntity<Set<String>> getAllTags(){
        Set<String> tags = fileService.getAllTags();
        return tags.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(tags);
    }

    @GetMapping(value = "searchFilesByTagKeyword/{keyword}")
    public ResponseEntity<List<File>> searchFilesByTagKeyword(@PathVariable String keyword){
        List<File> files = fileService.getFilesByTag(keyword);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }


    @GetMapping(value = "getFilesForTags")
    public ResponseEntity<List<File>> getFileForTags(@RequestBody List<Tag> tags) {
        List<File> files = fileService.getFilesByTag(tags);
        return !files.isEmpty() ? ResponseEntity.ok(files) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addTagsToFile/{id}")
    public ResponseEntity<String> addTagsToFile(@PathVariable String id, @RequestBody List<Tag> tags) {
        return fileService.addTagsToFile(id,tags) !=null ? ResponseEntity.ok("Tags added successfully") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addTagsToFile/{path}/{filename}")
    public ResponseEntity<String> addTagsToFile(@PathVariable String path,@PathVariable String filename, @RequestBody List<Tag> tags) {
        return fileService.addTagsToFile(path, filename, tags) != null ? ResponseEntity.ok("Tags added successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeTagsForFile/{id}")
    public ResponseEntity<String> removeTagsFromFile(@PathVariable String id,@RequestBody List<Tag> tags){
        return fileService.deleteTagsForFile(id,tags) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeTagsForFile/{path}/{filename}")
    public ResponseEntity<String> removeTagsFromFile(@PathVariable String path,@PathVariable String filename,@RequestBody List<Tag> tags){
        return fileService.deleteTagsForFile(path,filename,tags) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeAllTagsForFile/{id}")
    public ResponseEntity<String> removeAllTagsFromFile(@PathVariable String id){
        return fileService.deleteAllTagsForFile(id) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeAllTagsForFile/{path}/{filename}")
    public ResponseEntity<String> removeAllTagsFromFile(@PathVariable String path,@PathVariable String filename){
        return fileService.deleteAllTagsForFile(path,filename) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

}
