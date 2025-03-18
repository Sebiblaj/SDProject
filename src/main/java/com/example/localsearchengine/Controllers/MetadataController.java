package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.MetadataEntries;
import com.example.localsearchengine.Entites.Metadata;
import com.example.localsearchengine.Services.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "metadata")
public class MetadataController {

    @Autowired
    public MetadataService metadataService;

    @GetMapping(value = "getMetadataForFile/{id}")
    public ResponseEntity<Metadata> getMetadataForFile(@PathVariable String id) {
        Metadata metadata = metadataService.getMetadataForFile(id);
        return metadata != null ? ResponseEntity.ok(metadata) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getMetadataForFiles")
    public ResponseEntity<List<Metadata>> getMetadataForFiles(@RequestBody List<String> fileIds) {
        List<Metadata> metadataList = metadataService.getMetadataForFiles(fileIds);
        return metadataList != null ? ResponseEntity.ok(metadataList) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getMetadataForFile/{path}/{filename}")
    public ResponseEntity<Metadata> getMetadataForFile(@PathVariable String path, @PathVariable String filename) {
        Metadata metadata = metadataService.getMetadataForFile(path, filename);
        return metadata != null ? ResponseEntity.ok(metadata) : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "modifyMetadataForFile/{id}")
    public ResponseEntity<String> updateMetadataForFile(@PathVariable String id, @RequestBody List<MetadataEntries> entries) {
        return metadataService.modifyMetadataForFile(id,entries) != null ? ResponseEntity.ok("Metadata updated successfully") : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "modifyMetadataForFile/{path}/{filename}")
    public ResponseEntity<String> updateMetadataForFile(@PathVariable String path,@PathVariable String filename, @RequestBody List<MetadataEntries> entries) {
        return metadataService.modifyMetadataForFile(path,filename,entries) != null ? ResponseEntity.ok("Metadata updated successfully") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addMetadataToFile/{id}")
    public ResponseEntity<String> addMetadataToFile(@PathVariable String id,@RequestBody List<MetadataEntries> entries){
        return metadataService.addMetadataForFile(id,entries) !=null ? ResponseEntity.ok("Metadata added successfully") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "addMetadataToFile/{path}/{filename}")
    public ResponseEntity<String> addMetadataToFile(@PathVariable String path,@PathVariable String filename,@RequestBody List<MetadataEntries> entries){
        return metadataService.addMetadataForFile(path,filename,entries) != null ? ResponseEntity.ok("Metadata added successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "deleteMetadataForFile/{id}")
    public ResponseEntity<String> deleteMetadataForFile(@PathVariable String id,@RequestBody List<String> keys){
        return metadataService.deleteMetadataForFile(id,keys) != null ? ResponseEntity.ok("Metadata deleted successfully") : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "deleteMetadataForFile/{path}/{filename}")
    public ResponseEntity<String> deleteMetadataForFile(@PathVariable String path,@PathVariable String filename,@RequestBody List<String> keys){
        return metadataService.deleteMetadataForFile(path, filename, keys) != null ? ResponseEntity.ok("Metadata deleted successfully") : ResponseEntity.notFound().build();
    }
}
