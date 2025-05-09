package com.example.indexinglogger.Configuration;

import com.example.indexinglogger.Executors.FileProcessors.FileEntitiesProcessor;
import com.example.indexinglogger.Executors.HTTPProcessors.*;
import com.example.indexinglogger.Executors.RankingFunctions.FileRanker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Configurations {

    @Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }

    @Bean
    public ObjectMapper objectMapper() {return new ObjectMapper();}

    @Bean
    public HttpGetTypesHandler httpRequestHandler() { return new HttpGetTypesHandler(); }

    @Bean
    public HttpPostFilesHandler httpPostFilesHandler() { return new HttpPostFilesHandler(); }

    @Bean
    public FileEntitiesProcessor fileEntitiesProcessor() { return new FileEntitiesProcessor(); }

    @Bean
    public HttpPostContentsHandler httpPostContentsHandler() { return new HttpPostContentsHandler(); }

    @Bean
    public HttpPostMetadataHandler httpPostMetadataHandler() { return new HttpPostMetadataHandler(); }

    @Bean
    public HttpGetTagsFromOpenAI httpGetTagsFromOpenAI() { return new HttpGetTagsFromOpenAI(); }

    @Bean
    public HttpPostTagsHandler httpPostTagsHandler() { return new HttpPostTagsHandler(); }

    @Bean
    public FileRanker fileRanker() { return new FileRanker(); }
}
