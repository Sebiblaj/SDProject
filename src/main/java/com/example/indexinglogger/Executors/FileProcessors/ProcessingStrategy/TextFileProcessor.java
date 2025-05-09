package com.example.indexinglogger.Executors.FileProcessors.ProcessingStrategy;

import com.example.indexinglogger.DTOs.FileFullContents;
import com.example.indexinglogger.DTOs.FileTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TextFileProcessor implements FileProcessorStrategy {

    @Autowired
    private FileEntitiesHelper helper;

    private static final List<String> SUPPORTED = new ArrayList<>(List.of(
            "txt", "c", "cpp", "java", "yml", "yaml", "pl", "py","python","json","js", "ts",
            "html", "htm", "css", "scss", "xml", "json", "md", "ini", "cfg",
            "log", "sql", "bat", "sh", "tex", "r", "kt", "swift", "scala", "go",
            "rs", "properties", "toml", "gradle", "make", "mk", "env"
    ));

    @Override
    public boolean supports(String extension) {
        return SUPPORTED.contains(extension.toLowerCase());
    }

    @Override
    public FileFullContents process(Path path, int depth, List<FileTypeDTO> types,String extension) throws IOException, NoSuchAlgorithmException {
        return helper.processDefaultFile(path,depth,types,extension);
    }


}

