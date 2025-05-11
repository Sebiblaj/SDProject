package com.example.localsearchengine.WidgetsHandler;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@Component
public class WidgetsRegistry {

    private final Set<String> availableWidgets = new HashSet<>();

    @PostConstruct
    public void init() throws IOException {
        Path templatesDir = Paths.get("src/main/resources/templates/widgets");
        if (Files.exists(templatesDir)) {
            try (Stream<Path> paths = Files.walk(templatesDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".html"))
                        .forEach(p -> {
                            String name = p.getFileName().toString().replace(".html", "");
                            availableWidgets.add(name);
                        });
            }
        }
    }

    public String hasWidget(String name) {
        for(String widget : availableWidgets) {
            if (widget.contains(name)) return widget;
        }
        return null;
    }
}

