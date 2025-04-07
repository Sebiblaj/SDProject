package org.example.querymanager.ControllerLayer;

import org.example.querymanager.Entities.FileDTO;
import org.example.querymanager.ServiceLayer.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("bruteSearch")
public class Controller {

    @Autowired
    private QueryService queryService;

    @GetMapping(value = "getAll")
    public ResponseEntity<List<FileDTO>> getAll(@RequestParam String query) {
        List<FileDTO> files = queryService.getAllFiles(query);
        System.out.println(files);
        return !files.isEmpty() ? ResponseEntity.ok(files) : ResponseEntity.notFound().build();
    }
}
