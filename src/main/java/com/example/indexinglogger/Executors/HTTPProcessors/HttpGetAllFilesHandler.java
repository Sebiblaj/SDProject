package com.example.indexinglogger.Executors.HTTPProcessors;

import com.example.indexinglogger.DTOs.ReturnedFileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class HttpGetAllFilesHandler implements HttpProcessor<ReturnedFileDTO> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<ReturnedFileDTO> processList(Object ... args) {

        if(args[0] instanceof String path){

            ResponseEntity<List<ReturnedFileDTO>> response = restTemplate.exchange(
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
