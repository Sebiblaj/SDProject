package com.example.indexinglogger.Controller;

import com.example.indexinglogger.DTOs.PathDTO;
import com.example.indexinglogger.Service.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping(value = "indexing")
public class IndexingController {

    @Autowired
    private IndexingService indexingService;

    @PostMapping(value = "scanInDirectory")
    public ResponseEntity<String> triggerFullScan(@RequestBody PathDTO path) throws IOException {
        return ResponseEntity.ok(indexingService.scanAndSendFiles(path));
    }

    @GetMapping(value = "logs")
    public ResponseEntity<?> getIndexingLogs() {
        return ResponseEntity.ok(indexingService.getIndexingLog());
    }

//    @GetMapping(value = "logs/{id}")
//    public ResponseEntity<?> getIndexingLogs(@PathVariable String id) {
//        return ResponseEntity.ok(indexingService.getLatestLogs());
//    }
//
//    @GetMapping(value = "logs/{path}/{name}")
//    public ResponseEntity<?> getIndexingLogs(@PathVariable String path, @PathVariable String name) {
//        return ResponseEntity.ok(indexingService.getLatestLogs());
//    }
//
//    @GetMapping(value = "status/{path}/{name}")
//    public ResponseEntity<String> checkFileStatus(@PathVariable String path,@PathVariable String name) {
//        boolean isUpdated = indexingService.isFileUpToDate(filePath);
//        return ResponseEntity.ok(isUpdated ? "File is up-to-date" : "File has changed, needs reindexing.");
//    }
//
//    @GetMapping(value = "status/{id}")
//    public ResponseEntity<String> checkFileStatus(@PathVariable String id) {
//        boolean isUpdated = indexingService.isFileUpToDate(filePath);
//        return ResponseEntity.ok(isUpdated ? "File is up-to-date" : "File has changed, needs reindexing.");
//    }
//
//    /** ✅ 4. Force re-indexing of a specific file */
//    @PostMapping(value = "reindex/{id}")
//    public ResponseEntity<String> reIndexFile(@PathVariable String id) {
//        indexingService.reIndexFile(filePath);
//        return ResponseEntity.ok("Re-indexing completed for: " + filePath);
//    }
//
//    @PostMapping(value = "reindex/{path}/{name}")
//    public ResponseEntity<String> reIndexFile(@PathVariable String path,@PathVariable String name) {
//        indexingService.reIndexFile(filePath);
//        return ResponseEntity.ok("Re-indexing completed for: " + filePath);
//    }
}
