package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.ContentDTOS.ContentsDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileContentDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileSearchResult;
import com.example.localsearchengine.DTOs.FileSearchCriteria;
import com.example.localsearchengine.Services.FileContentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "contents")
public class FileContentsController {

    @Autowired
    private FileContentsService fileContentsService;


    @GetMapping(value = "",params = {"filePath","fileName","extension"})
    public ResponseEntity<String> getFileByPathAndName(@RequestParam String filePath,
                                                       @RequestParam String fileName,
                                                       @RequestParam String extension) {
        String fileContents = fileContentsService.getFileContents(filePath, fileName,extension);
        return fileContents !=null ? ResponseEntity.ok(fileContents) : ResponseEntity.notFound().build();
    }


    @GetMapping(value = "preview",params = {"filePath","fileName","extension"})
    public ResponseEntity<String> getPreviewById(@RequestParam String filePath,
                                                 @RequestParam String fileName,
                                                 @RequestParam String extension) {
        String preview = fileContentsService.getPreview(filePath, fileName,extension);
        return preview != null ? ResponseEntity.ok(preview) : ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<FileSearchResult>> searchFiles(@RequestBody FileSearchCriteria criteria) {
        List<FileSearchResult> results = fileContentsService.search(criteria);
        return results == null || results.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(results);
    }

    @PostMapping(value = "add",params = {"filePath","fileName","extension"})
    public ResponseEntity<String> setFileContentsById(@RequestParam String filePath,
                                                      @RequestParam String fileName,
                                                      @RequestParam String extension,
                                                      @RequestBody ContentsDTO contentsDTO) {
        String fileContents = fileContentsService.setFileContents(filePath,fileName,extension,contentsDTO);
        return fileContents.equals("Success") ? ResponseEntity.ok("Contents added successfully to the file.") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "add")
    public ResponseEntity<String> setFileContentsById(@RequestBody List<FileContentDTO> fileContentDTOS) {
        String fileContents = fileContentsService.setFileContents(fileContentDTOS);
        return fileContents.equals("Contents Added Successfully")  ? ResponseEntity.ok("Contents added successfully") : ResponseEntity.ok("Could not load contents");
    }

    @DeleteMapping(value = "delete",params = {"filePath","fileName","extension"})
    public ResponseEntity<String> deleteContentsForPathAndFilename(@RequestParam String filePath,
                                                                   @RequestParam String fileName,
                                                                   @RequestParam String extension) {
        fileContentsService.deleteFileContents(filePath, fileName, extension);
        return ResponseEntity.ok("File Contents Deleted");
    }

}
