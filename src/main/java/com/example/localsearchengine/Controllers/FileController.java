package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.TagsList;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.DTOs.PathAndName;
import com.example.localsearchengine.Entites.FileType;
import com.example.localsearchengine.Services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/file")
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

    @GetMapping(value = "findFileById/{id}")
    public ResponseEntity<File> getFileById(@PathVariable int id) {
        File file = fileService.getFileById(id);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addFile")
    public ResponseEntity<File> addFile(@RequestBody File file) {
        File savedFile = fileService.addFile(file);
        return ResponseEntity.ok(savedFile);
    }

    @PostMapping(value = "addMultipleFiles")
    public ResponseEntity<String> addMultipleFiles(@RequestBody List<File> files) {
        String response = fileService.addMultipleFiles(files);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("updateFile/{id}")
    public ResponseEntity<File> updateFilename(@PathVariable int id, @RequestBody Map<String, String> request) {
        File responseFile = fileService.updateFile(id, request);
        return responseFile != null ? ResponseEntity.ok(responseFile) : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "deleteFileById/{id}")
    public ResponseEntity<String> deleteFileById(@PathVariable int id) {
        String result = fileService.deleteFileById(id);
        return result.equals("File deleted") ? ResponseEntity.ok("File deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/deleteMultipleFilesById")
    public ResponseEntity<String> deleteMultipleFiles(@RequestBody List<String> fileIds) {
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
    public ResponseEntity<TagsList> getTagsForFile(@PathVariable String id) {
        TagsList tags = fileService.getTagsForFile(id);
        return tags != null ? ResponseEntity.ok(tags) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getTagsForFile/{path}/{filename}")
    public ResponseEntity<TagsList> getTagsForFile(@PathVariable String path, @PathVariable String filename) {
        TagsList tags = fileService.getTagsForFile(path, filename);
        return tags != null ? ResponseEntity.ok(tags) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getAllTags")
    public ResponseEntity<Set<String>> getAllTags(){
        Set<String> tags = fileService.getAllTags();
        return tags.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(tags);
    }

    @GetMapping(value = "searchFilesByTagKeyword")
    public ResponseEntity<List<File>> searchFilesByTagKeyword(@RequestParam String keyword){
        List<File> files = fileService.getFilesByTag(keyword);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }


    @GetMapping(value = "getFilesForTags")
    public ResponseEntity<List<File>> getFileForTags(@RequestBody TagsList tags) {
        List<File> files = fileService.getFilesByTag(tags);
        return !files.isEmpty() ? ResponseEntity.ok(files) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addTagsToFile/{id}")
    public ResponseEntity<String> addTagsToFile(@PathVariable String id, @RequestBody TagsList tags) {
        return fileService.addTagsToFile(id,tags) !=null ? ResponseEntity.ok("Tags added successfully") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addTagsToFile/{path}/{filename}")
    public ResponseEntity<String> addTagsToFile(@PathVariable String path,String filename, @RequestBody TagsList tags) {
        return fileService.addTagsToFile(path, filename, tags) != null ? ResponseEntity.ok("Tags added successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeTagsForFile/{id}")
    public ResponseEntity<String> removeTagsFromFile(@PathVariable String id,@RequestBody TagsList tags){
        return fileService.deleteTagsForFile(id,tags) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeTagsForFile/{path}/{filename}")
    public ResponseEntity<String> removeTagsFromFile(@PathVariable String path,String filename,@RequestBody TagsList tags){
        return fileService.deleteTagsForFile(path,filename,tags) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeAllTagsForFile/{id}")
    public ResponseEntity<String> removeAllTagsFromFile(@PathVariable String id){
        return fileService.deleteAllTagsForFile(id) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "removeAllTagsForFile/{path}/{filename}")
    public ResponseEntity<String> removeAllTagsFromFile(@PathVariable String path,String filename){
        return fileService.deleteAllTagsForFile(path,filename) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "replaceTagsForFile/{id}")
    public ResponseEntity<String> replaceTagsForFile(@PathVariable String id, @RequestBody TagsList newTags){
        return fileService.replaceTagsForFile(id,newTags) !=null ? ResponseEntity.ok("Tags replaced successfully") : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "replaceTagsForFile/{path}/{filename}")
    public ResponseEntity<String> replaceTagsForFile(@PathVariable String path,String filename, @RequestBody TagsList newTags){
        return fileService.replaceTagsForFile(path,filename,newTags) != null ? ResponseEntity.ok("Tags replaced successfully") : ResponseEntity.notFound().build();
    }



}
