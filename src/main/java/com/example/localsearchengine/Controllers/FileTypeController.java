package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.FileDTOS.FileTypeDTO;
import com.example.localsearchengine.Services.FileTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("type")
public class FileTypeController {

    @Autowired
    private FileTypeService fileTypeService;

    @GetMapping(value = "")
    public ResponseEntity<List<FileTypeDTO>> getAllTypesNoId() {return ResponseEntity.ok(fileTypeService.getFileTypesNoId());}

    @GetMapping(value = "check")
    public ResponseEntity<Boolean> checkTypeExists(@RequestParam String ext) {
        return fileTypeService.checkFileType(ext) ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "add")
    public ResponseEntity<String> addFileType(@RequestParam String ext) {
        return ResponseEntity.ok(fileTypeService.saveFileType(ext));
    }

    @DeleteMapping(value = "delete")
    public ResponseEntity<String> deleteFileType(@RequestParam List<String> ext) {
        return ResponseEntity.ok(fileTypeService.deleteFileType(ext));
    }

}
