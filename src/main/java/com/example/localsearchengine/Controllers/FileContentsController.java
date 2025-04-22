package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.ContentDTOS.ContentsDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileContentDTO;
import com.example.localsearchengine.DTOs.ContentDTOS.FileSearchResult;
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


    @GetMapping(value = "",params = {"filePath","fileName"})
    public ResponseEntity<String> getFileByPathAndName(@RequestParam String filePath,
                                                       @RequestParam String fileName) {
        String fileContents = fileContentsService.getFileContents(filePath, fileName);
        return fileContents !=null ? ResponseEntity.ok(fileContents) : ResponseEntity.notFound().build();
    }


    @GetMapping(value = "preview",params = {"filePath","fileName"})
    public ResponseEntity<String> getPreviewById(@RequestParam String filePath,
                                                 @RequestParam String fileName) {
        String preview = fileContentsService.getPreview(filePath, fileName);
        return preview != null ? ResponseEntity.ok(preview) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "search")
    public ResponseEntity<List<FileSearchResult>> searchFiles(
            @RequestParam(required = false) List<String> filePath,
            @RequestParam(required = false) List<String> fileName,
            @RequestParam(required = false) List<String> content) {

        List<FileSearchResult> results = fileContentsService.searchInFileByPathAndName(filePath, fileName, content);
        return results == null || results.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(results);
    }

    @PostMapping(value = "add",params = {"filePath","fileName"})
    public ResponseEntity<String> setFileContentsById(@RequestParam String filePath,
                                                      @RequestParam String fileName,
                                                      @RequestBody ContentsDTO contentsDTO) {
        String fileContents = fileContentsService.setFileContents(filePath,fileName,contentsDTO);
        return fileContents.equals("Success") ? ResponseEntity.ok("Contents added successfully to the file.") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "add")
    public ResponseEntity<String> setFileContentsById(@RequestBody List<FileContentDTO> fileContentDTOS) {
        System.out.println("Reached here");
        String fileContents = fileContentsService.setFileContents(fileContentDTOS);
        return fileContents.equals("File Content Added Successfully")  ? ResponseEntity.ok("Contents added successfully") : ResponseEntity.ok("Could not load contents");
    }

    @DeleteMapping(value = "delete",params = {"filePath","fileName"})
    public ResponseEntity<String> deleteContentsForPathAndFilename(@RequestParam String filePath,
                                                                   @RequestParam String fileName) {
        fileContentsService.deleteFileContents(filePath, fileName);
        return ResponseEntity.ok("File Contents Deleted");
    }

}
