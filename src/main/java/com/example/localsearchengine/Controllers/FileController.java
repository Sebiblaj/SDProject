package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.FileDTOS.PathAndName;
import com.example.localsearchengine.DTOs.FileDTOS.ReturnedFileDTO;
import com.example.localsearchengine.DTOs.FileDTOS.Tag;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(value = "")
    public ResponseEntity<List<ReturnedFileDTO>> getFiles() {
        List<ReturnedFileDTO> files = fileService.getFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping(value = "search", params = {"fileName", "filePath"})
    public ResponseEntity<ReturnedFileDTO> getFileById(@RequestParam String filePath,
                                            @RequestParam String fileName) {
        ReturnedFileDTO file = fileService.getFile(fileName, filePath);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "search", params = "fileName")
    public ResponseEntity<List<ReturnedFileDTO>> searchFiles(@RequestParam String fileName) {
        List<ReturnedFileDTO> files = fileService.searchFilesByName(fileName);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }

    @GetMapping(value = "search", params = "extension")
    public ResponseEntity<List<ReturnedFileDTO>> getFilesByExtension(@RequestParam String extension) {
        List<ReturnedFileDTO> files = fileService.searchFilesByExtension(extension);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }

    @GetMapping(value = "between", params = {"min","max"})
    public ResponseEntity<List<ReturnedFileDTO>> findBySizeInterval(@RequestParam String min,
                                                                    @RequestParam String max) {
        List<ReturnedFileDTO> files = fileService.findBySizeInterval(min, max);
        return files.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(files);
    }

    @PostMapping("add")
    public ResponseEntity<String> addFileFlexible(@RequestBody Object payload) {
        String response = fileService.addFile(payload);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.ok("Could not add files");

    }

    @PutMapping("update")
    public ResponseEntity<String> updateFilename(@RequestParam String filePath,
                                               @RequestParam String fileName,
                                               @RequestBody List<MetadataEntries> request) {
        String result = fileService.updateFile(filePath,fileName, request);
        return !result.equals("File not found") ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "delete")
    public ResponseEntity<String> deleteMultipleFilesByPathAndName(@RequestBody List<PathAndName> fileIds) {
        boolean result = fileService.deleteByPathAndFilename(fileIds);
        return result ? ResponseEntity.ok("All files deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "delete",params = "all")
    public ResponseEntity<String> deleteMultipleFilesByPathAndName() {
        fileService.deleteByPathAndFilename();
        return ResponseEntity.ok("All files deleted successfully");
    }

/*
The next part is for the Tags of the file
*/


    @GetMapping(value = "tags",params = {"filePath","fileName"})
    public ResponseEntity<List<Tag>> getTagsForFile(@RequestParam String filePath,
                                                    @RequestParam String fileName) {
        List<Tag> tags = fileService.getTagsForFile(filePath, fileName);
        return tags != null ? ResponseEntity.ok(tags) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "tags")
    public ResponseEntity<Set<String>> getAllTags(){
        Set<String> tags = fileService.getAllTags();
        return tags.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(tags);
    }


    @GetMapping(value = "tags/file",params = "tags")
    public ResponseEntity<List<ReturnedFileDTO>> getFileForTags(@RequestParam List<String> tags) {
        List<ReturnedFileDTO> files = fileService.getFilesByTag(tags);
        return !files.isEmpty() ? ResponseEntity.ok(files) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "tags/add" , params = {"filePath","fileName"})
    public ResponseEntity<String> addTagsToFile(@RequestParam String filePath,
                                                @RequestParam String fileName,
                                                @RequestBody List<Tag> tags) {
        return fileService.addTagsToFile(filePath,fileName, tags) != null ? ResponseEntity.ok("Tags added successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "tags/delete",params = {"filePath,fileName"})
    public ResponseEntity<String> removeTagsFromFile(@RequestParam String filePath,
                                                     @RequestParam String fileName,
                                                     @RequestBody List<Tag> tags){
        return fileService.deleteTagsForFile(filePath,fileName,tags) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "tags/delete",params = {"filePath","fileName"})
    public ResponseEntity<String> removeAllTagsFromFile(@RequestParam String filePath,
                                                        @RequestParam String fileName){
        return fileService.deleteAllTagsForFile(filePath,fileName) != null ? ResponseEntity.ok("Tags deleted successfully") : ResponseEntity.notFound().build();
    }

}
