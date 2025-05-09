package com.example.indexinglogger.Executors.FileProcessors.ProcessingStrategy;

import com.example.indexinglogger.DTOs.FileFullContents;
import com.example.indexinglogger.DTOs.FileTypeDTO;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface FileProcessorStrategy {
    boolean supports(String extension);
    FileFullContents process(Path path, int depth, List<FileTypeDTO> types,String extension) throws IOException, NoSuchAlgorithmException;
}

