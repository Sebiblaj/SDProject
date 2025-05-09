package org.example.openaichat.Controller;

import org.example.openaichat.Entities.FileContentDTO;
import org.example.openaichat.Entities.OpenAIResponse;
import org.example.openaichat.Service.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "openai")
public class GPTController {

    @Autowired
    private GPTService gptService;

    @PostMapping(value = "tags")
    public ResponseEntity<List<OpenAIResponse>> getTags(@RequestBody List<FileContentDTO> contents) {
        Mono<List<OpenAIResponse>> responses = gptService.getCompletion(contents);

        for(OpenAIResponse response : Objects.requireNonNull(responses.block())){
            System.out.println("Tags "+response.getTags());
        }

        return responses
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())).block();
    }


}
