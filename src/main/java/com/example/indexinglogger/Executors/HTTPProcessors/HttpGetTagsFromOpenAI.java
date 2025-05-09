package com.example.indexinglogger.Executors.HTTPProcessors;

import com.example.indexinglogger.DTOs.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class HttpGetTagsFromOpenAI implements HttpProcessor<OpenAIResponse> {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<OpenAIResponse> processList(Object... args) {

        if (args[0] instanceof String path && args[1] instanceof List<?>) {
            List<FileContentDTO> fileContentDTO = objectMapper.convertValue(args[1], new TypeReference<>() {});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<FileContentDTO>> requestEntity = new HttpEntity<>(fileContentDTO, headers);

            ResponseEntity<List<OpenAIResponse>> response;
            try {
                response = restTemplate.exchange(
                        path,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<>() {}
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody();
                } else {
                    return Collections.emptyList();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }
}
