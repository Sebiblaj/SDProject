package org.example.openaichat.Service;

import jakarta.annotation.PostConstruct;
import org.example.openaichat.Entities.ChatCompletionResponse;
import org.example.openaichat.Entities.FileContentDTO;
import org.example.openaichat.Entities.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

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

    public Mono<List<OpenAIResponse>> getCompletion(List<FileContentDTO> contents) {

        Flux<OpenAIResponse> responseFlux = Flux.fromIterable(contents)
                .flatMap(this::processFile);

        return responseFlux.collectList();
    }

    private Mono<OpenAIResponse> processFile(FileContentDTO content) {

        String systemMessage = "You are a helpful assistant.";

        List<Map<String, String>> messages = getMaps(content, systemMessage);

        return webClient.post()
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", messages
                ))
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .map(chatResponse -> {
                    String contentText = chatResponse.getChoices().getFirst().getMessage().getContent();
                    List<String> tags = List.of(contentText.split("\\s*,\\s*"));
                    return new OpenAIResponse(
                            content.getFilename(),
                            content.getPath(),
                            content.getExtension(),
                            tags
                    );
                })
                .onErrorResume(WebClientResponseException.class, e -> Mono.just(new OpenAIResponse(
                        content.getFilename(),
                        content.getPath(),
                        content.getExtension(),
                        List.of("Error: " + e.getMessage())
                )));

    }

    private static List<Map<String, String>> getMaps(FileContentDTO content, String systemMessage) {
        String userMessage = String.format(
                "I am working on a file search engine and I want some help from you. " +
                        "I want to store some tags for the files I am indexing, like some words that define the " +
                        "topic of the file and what it is about. For example, if it's a C file with some code, return " +
                        "some tags that help the user understand what it is and what it does in general in words like: " +
                        "Cprogramming, sorting, indexing. For text files, do the same: words that summarize its contents. " +
                        "Return only the words you have found, no additional comments from you, the format that I expect: " +
                        "tag1, tag2, tag3, ... " +
                        "Now I will give you the name of the file, which might be an important clue about what's in the file: " +
                        "%s and the following will be the contents of the file that you should analyze and return those tags: %s",
                content.getFilename(), content.getContent());

        return List.of(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", userMessage)
        );
    }
}
