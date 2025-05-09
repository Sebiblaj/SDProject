package com.example.indexinglogger.Controller;

import com.example.indexinglogger.DTOs.PathDTO;
import com.example.indexinglogger.Service.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping(value = "indexer")
public class IndexingController {

    @Autowired
    private IndexingService indexingService;

    @PostMapping(value = "scanInDirectory")
    public ResponseEntity<String> triggerFullScan(@RequestBody PathDTO path) throws IOException {
        return ResponseEntity.ok(indexingService.scanAndSendFiles(path));
    }

}
