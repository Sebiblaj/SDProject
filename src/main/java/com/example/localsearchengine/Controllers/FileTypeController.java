package com.example.localsearchengine.Controllers;

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

    @GetMapping(value = "getAllTypesMatching")
    public List<FileType> getAllTypesMatching(@RequestBody String keyword) {return fileTypeService.getFileTypesMatching(keyword);}

    @PostMapping(value = "addFileType")
    public ResponseEntity<String> addFileType(@RequestBody String fileType) {
        return ResponseEntity.ok(fileTypeService.saveFileType(fileType));
    }

    @DeleteMapping(value = "deleteFileType")
    public ResponseEntity<String> deleteFileType(@RequestBody String fileType){
        return ResponseEntity.ok(fileTypeService.deleteFileType(fileType));
    }

}
