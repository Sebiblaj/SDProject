package com.example.indexinglogger.Executors.HTTPProcessors;

import com.example.indexinglogger.DTOs.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class HttpPostFilesHandler implements HttpProcessor<String>{

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String process(Object ... args) {

        if( args[0] instanceof String path && args[1] instanceof List){
            List<FileDTO> files = (List<FileDTO>) args[1];

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<List<FileDTO>> entity = new HttpEntity<>(files,headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    path,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        }

        return null;
    }

}
