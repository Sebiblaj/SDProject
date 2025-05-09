package com.example.indexinglogger.Executors.FileProcessors.ProcessingStrategy;

import com.example.indexinglogger.DTOs.FileFullContents;
import com.example.indexinglogger.DTOs.FileTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

@Component
public class ImageFileProcessor implements FileProcessorStrategy {

    @Autowired
    private FileEntitiesHelper helper;

    private static final Set<String> SUPPORTED = Set.of("jpg", "jpeg", "png", "gif","bmp");

    @Override
    public boolean supports(String extension) {
        return SUPPORTED.contains(extension.toLowerCase());
    }

    @Override
    public FileFullContents process(Path path, int depth, List<FileTypeDTO> types,String extension) throws IOException, NoSuchAlgorithmException {
        return helper.processImageFile(path,depth,types,extension);
    }

}

