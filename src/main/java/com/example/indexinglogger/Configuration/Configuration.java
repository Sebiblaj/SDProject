package com.example.indexinglogger.Configuration;

import com.example.indexinglogger.Executors.FileProcessors.FileEntitiesProcessor;
import com.example.indexinglogger.Executors.HTTPProcessors.HttpGetTypesHandler;
import com.example.indexinglogger.Executors.HTTPProcessors.HttpPostContentsHandler;
import com.example.indexinglogger.Executors.HTTPProcessors.HttpPostFilesHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }

    @Bean
    public HttpGetTypesHandler httpRequestHandler() { return new HttpGetTypesHandler(); }

    @Bean
    public HttpPostFilesHandler httpPostFilesHandler() { return new HttpPostFilesHandler(); }

    @Bean
    public FileEntitiesProcessor fileEntitiesProcessor() { return new FileEntitiesProcessor(); }

    @Bean
    public HttpPostContentsHandler httpPostContentsHandler() { return new HttpPostContentsHandler(); }
}
