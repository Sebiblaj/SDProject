package org.example.openaichat.Service;

import jakarta.annotation.PostConstruct;
import org.example.openaichat.Entities.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.url}")
    private String apiUrl;

    private WebClient webClient;

    public GPTService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    @PostConstruct
    public void init() {
        this.webClient = this.webClient.mutate()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .baseUrl(apiUrl)
                .build();
    }

    public Mono<String> getCompletion(String filename, String contents) {
        String systemMessage = "You are a helpful assistant.";

        String userMessage = "I am working on a file search engine and I want some help from you. " +
                "I want to store some tags for the files I am indexing, like some words that define the " +
                "topic of the file and what it is about. For example, if it's a C file with some code, return " +
                "some tags that help the user understand what it is and what it does in general in words like: " +
                "Cprogramming, sorting, indexing. For text files, do the same: words that summarize its contents. " +
                "Return only the words you have found, no additional comments from you, the format that I expect: " +
                "tag1, tag2, tag3, ... " +
                "Now I will give you the name of the file, which might be an important clue about what's in the file: " +
                filename + " and the following will be the contents of the file that you should analyze and return those tags: " + contents;

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", userMessage)
        );


        return webClient.post()
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", messages
                ))
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .map(response -> response.getChoices().getFirst().getMessage().getContent().getContents())
                .onErrorResume(WebClientResponseException.class, e -> Mono.just("Error: " + e.getMessage()));
    }
}
