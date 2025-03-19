package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.DTOs.FileTypeDTO;
import com.example.localsearchengine.Entites.FileType;
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

    @GetMapping(value = "getAllTypes")
    public ResponseEntity<List<FileType>> getAllTypes() {return ResponseEntity.ok(fileTypeService.getFileTypes());}

    @GetMapping(value = "checkTypeExists")
    public ResponseEntity<Boolean> checkTypeExists(@RequestBody FileTypeDTO fileTypeDTO) {
        return fileTypeService.checkFileType(fileTypeDTO) ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "getAllTypesMatching")
    public List<FileType> getAllTypesMatching(@RequestBody FileTypeDTO fileTypeDTO) {return fileTypeService.getFileTypesMatching(fileTypeDTO);}

    @PostMapping(value = "addFileType")
    public ResponseEntity<String> addFileType(@RequestBody FileTypeDTO fileTypeDTO) {
        return ResponseEntity.ok(fileTypeService.saveFileType(fileTypeDTO));
    }

    @DeleteMapping(value = "deleteFileType")
    public ResponseEntity<String> deleteFileType(@RequestBody FileTypeDTO fileTypeDTO) {
        return ResponseEntity.ok(fileTypeService.deleteFileType(fileTypeDTO));
    }

}
