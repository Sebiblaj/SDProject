package com.example.logger.Configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Configuration
public class Config {

    @Bean
    public File file() {
        return new File("logs/SystemLogger.txt");
    }

    @Bean
    public File fileIndex() {
        return new File("logs/SystemIndex.txt");
    }

    @Bean
    public FileWriter fileWriterIndex() throws IOException {
        return new FileWriter(fileIndex(), true);
    }

    @Bean
    public FileWriter fileWriter() throws IOException {
        return new FileWriter(file(), true);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
