package org.example.openaichat.Entities;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatCompletionResponse {
    private List<Choice> choices;

    @Getter
    public static class Choice {
        private Message message;

    }

    @Getter
    public static class Message {
        private String content;

    }
}
