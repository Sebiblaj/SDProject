package com.example.indexinglogger.Executors.HTTPProcessors;

import com.example.indexinglogger.DTOs.FileTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class HttpGetTypesHandler implements HttpProcessor<FileTypeDTO> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<FileTypeDTO> processList(Object ... args) {

        if(args[0] instanceof String path){

            ResponseEntity<List<FileTypeDTO>> response = restTemplate.exchange(
                    path,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        }

        return null;
    }


}
