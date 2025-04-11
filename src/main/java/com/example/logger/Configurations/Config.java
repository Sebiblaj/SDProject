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
    public File file(){ return new File("SystemLogger.txt"); }

    @Bean
    public FileWriter fileWriter(File file) throws IOException { return new FileWriter(file);}

    @Bean
    public ObjectMapper objectMapper(){ return new ObjectMapper(); }
}
