package com.example.localsearchengine.SpellingCorrector;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class SpellingCorrector {

    private final Map<String, Integer> wordMap;

    public static final List<Character> SPECIAL_CHARACTERS = Arrays.asList(
            '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
            ':', ';', '<', '=', '>', '?', '@', '[', ']', '^', '_', '`', '{', '|', '}', '~'
    );

    public SpellingCorrector() {
        this.wordMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream("big.txt"), StandardCharsets.UTF_8))) {

            int i;
            StringBuilder sb = new StringBuilder();

            while ((i = reader.read()) != -1) {
                char c = (char) i;

                if (SPECIAL_CHARACTERS.contains(c)) continue;

                if (Character.isLetterOrDigit(c)) {
                    sb.append(c);
                } else {
                    if (!sb.isEmpty() && sb.length() > 2) {
                        String word = sb.toString().toLowerCase();
                        this.wordMap.put(word, this.wordMap.getOrDefault(word, 0) + 1);
                        sb.setLength(0);
                    } else {
                        sb.setLength(0);
                    }
                }
            }

            if (!sb.isEmpty() && sb.length() > 2) {
                String word = sb.toString().toLowerCase();
                this.wordMap.put(word, this.wordMap.getOrDefault(word, 0) + 1);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }
    }

    public List<String> returnSuggestions(String word) {
        String lowerCaseWord = word.toLowerCase();

        if (wordMap.containsKey(lowerCaseWord)) {
            return new ArrayList<>();
        }

        Set<String> visited = new HashSet<>();
        Set<String> validSuggestions = new HashSet<>();
        int maxDepth = 2;

        generateSuggestionsRecursive(lowerCaseWord, 0, maxDepth, visited, validSuggestions);

        List<String> result = new ArrayList<>(validSuggestions);
        result.sort((a, b) -> wordMap.get(b) - wordMap.get(a));

        return result;
    }

    private void generateSuggestionsRecursive(String word, int depth, int maxDepth,
                                              Set<String> visited, Set<String> validSuggestions) {
        if (depth > maxDepth || visited.contains(word)) return;

        visited.add(word);

        List<String> edits = new ArrayList<>();
        edits.addAll(checkReplaceChar(word));
        edits.addAll(insertChar(word));
        edits.addAll(deleteChar(word));
        edits.addAll(transposeChar(word));

        for (String edit : edits) {
            if (wordMap.containsKey(edit)) {
                validSuggestions.add(edit);
            }
            generateSuggestionsRecursive(edit, depth + 1, maxDepth, visited, validSuggestions);
        }
    }


    // Replace each character with a-z
    private List<String> checkReplaceChar(String word) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (word.charAt(i) == c) continue;
                StringBuilder sb = new StringBuilder(word);
                sb.setCharAt(i, c);
                suggestions.add(sb.toString());
            }
        }

        return suggestions;
    }

    // Insert a-z after each character
    private List<String> insertChar(String word) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                StringBuilder sb = new StringBuilder(word);
                sb.insert(i, c);
                suggestions.add(sb.toString());
            }
        }

        return suggestions;
    }

    // Delete each character
    private List<String> deleteChar(String word) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            StringBuilder sb = new StringBuilder(word);
            sb.deleteCharAt(i);
            suggestions.add(sb.toString());
        }

        return suggestions;
    }

    // Swap adjacent characters
    private List<String> transposeChar(String word) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 0; i < word.length() - 1; i++) {
            StringBuilder sb = new StringBuilder(word);
            char temp = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(i + 1));
            sb.setCharAt(i + 1, temp);
            suggestions.add(sb.toString());
        }

        return suggestions;
    }
}