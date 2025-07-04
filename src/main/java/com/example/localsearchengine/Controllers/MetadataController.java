package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.MetadataDTOS.KeyDTO;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataDTO;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataEntries;
import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataPathNameDTO;
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

    @GetMapping(value = "")
    public ResponseEntity<MetadataDTO> getMetadataForFile(@RequestParam String filePath,
                                                          @RequestParam String fileName,
                                                          @RequestParam String extension) {
        MetadataDTO metadata = metadataService.getMetadataForFile(filePath, fileName,extension);
        return metadata != null ? ResponseEntity.ok(metadata) : ResponseEntity.notFound().build();
    }


    @PutMapping(value = "modify")
    public ResponseEntity<String> updateMetadataForFile(@RequestParam String filePath,
                                                        @RequestParam String fileName,
                                                        @RequestParam String extension,
                                                        @RequestBody List<MetadataEntries> entries) {
        return metadataService.modifyMetadataForFile(filePath,fileName,extension,entries) != null ? ResponseEntity.ok("Metadata updated successfully") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "add",params = {"filePath","fileName","extension"})
    public ResponseEntity<String> addMetadataToFile(@RequestParam String filePath,
                                                    @RequestParam String fileName,
                                                    @RequestParam String extension,
                                                    @RequestBody List<MetadataEntries> entries){
        return metadataService.addMetadataForFile(filePath,fileName,extension,entries) != null ? ResponseEntity.ok("Metadata added successfully") : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "add")
    public ResponseEntity<String> addMetadataToFile(@RequestBody List<MetadataPathNameDTO> metadataPathNameDTOS){
        return metadataService.addMultipleMetadata(metadataPathNameDTOS) != null ? ResponseEntity.ok("Metadata added successfully") : ResponseEntity.ok("Could not add metadata");
    }

    @DeleteMapping(value = "delete",params = {"filePath","fileName","extension"})
    public ResponseEntity<String> deleteMetadataForFile(@RequestParam String filePath,
                                                        @RequestParam String fileName,
                                                        @RequestParam String extension,
                                                        @RequestBody List<KeyDTO> keys){
        return metadataService.deleteMetadataForFile(filePath,fileName,extension, keys) != null ? ResponseEntity.ok("Metadata deleted successfully") : ResponseEntity.notFound().build();
    }
}
