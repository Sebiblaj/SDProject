package org.example.openaichat.Controller;

import org.example.openaichat.Entities.Content;
import org.example.openaichat.Service.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "openai")
public class GPTController {

    @Autowired
    private GPTService gptService;

    @PostMapping(value = "tags",params = "fileName")
    public Mono<String> getTags(@RequestParam String fileName,@RequestBody Content contents) {
        return gptService.getCompletion(fileName,contents.getContents());
    }
}
