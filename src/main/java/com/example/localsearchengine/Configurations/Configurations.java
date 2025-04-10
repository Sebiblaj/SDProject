package com.example.localsearchengine.Configurations;

import com.example.localsearchengine.ServiceExecutors.FileConvertor;
import com.example.localsearchengine.ServiceExecutors.MetadataConvertor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configurations {

    @Bean
    public ObjectMapper objectMapper(){ return new ObjectMapper(); }

    @Bean
    public FileConvertor fileConvertor(){ return new FileConvertor(); }

    @Bean
    public MetadataConvertor metadataConvertor(){ return new MetadataConvertor(); }
}
