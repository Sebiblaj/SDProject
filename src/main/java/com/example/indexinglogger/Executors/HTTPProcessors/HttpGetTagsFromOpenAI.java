package com.example.indexinglogger.Executors.HTTPProcessors;

import com.example.indexinglogger.DTOs.Content;
import com.example.indexinglogger.DTOs.FileContentDTO;
import com.example.indexinglogger.DTOs.FileFullContents;
import com.example.indexinglogger.DTOs.FileTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Component
public class HttpGetTagsFromOpenAI implements HttpProcessor<String> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<String> processList(Object ... args) {

        if(args[0] instanceof String path && args[1] instanceof FileContentDTO fileContentDTO) {

            String filename = fileContentDTO.getFilename();
            Content contents = new Content(fileContentDTO.getContent());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Content> requestEntity = new HttpEntity<>(contents, headers);

            path = path.concat("?fileName=").concat(filename);

            System.out.println(path);

            ResponseEntity<String> response = restTemplate.exchange(
                    path,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return Collections.singletonList(response.getBody());
            }
        }
        return null;
    }
}
