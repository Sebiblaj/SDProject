package com.example.localsearchengine.WidgetsHandler.StrategyHandlers;

import com.example.localsearchengine.DTOs.FileSearchCriteria;
import com.example.localsearchengine.Services.FileContentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CodeViewerWidgetHandler implements WidgetHandler {

    private static final String CODE_EDITOR_WIDGET_TEMPLATE = "code";

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private FileContentsService fileContentsService;

    @Override
    public boolean isWidgetValid(String widgetName) {
        return widgetName.equals(CODE_EDITOR_WIDGET_TEMPLATE);
    }

    @Override
    public String getWidgetPage(String widgetName, Context context, Object payload) {
        if (payload instanceof FileSearchCriteria fileSearchCriteria) {
            List<String> paths = fileSearchCriteria.getPaths();
            List<String> names = fileSearchCriteria.getNames();
            List<String> extensions = fileSearchCriteria.getKeywords();

            List<Map<String, String>> files = new ArrayList<>();

            for (int i = 0; i < paths.size(); i++) {
                String fullContent = fileContentsService.getFileContents(paths.get(i), names.get(i), extensions.get(i));
                String language = mapExtensionToLanguage(extensions.get(i));

                Map<String, String> fileData = new HashMap<>();
                fileData.put("name", names.get(i));
                fileData.put("content", fullContent);
                fileData.put("language", language);

                files.add(fileData);
            }

            context.setVariable("fileEntities", files);
            return templateEngine.process("widgets/" + widgetName, context);
        }

        return null;
    }

    private String mapExtensionToLanguage(String ext) {
        return switch (ext.toLowerCase()) {
            case "java" -> "java";
            case "c", "cpp", "h" -> "cpp";
            case "js" -> "javascript";
            case "ts" -> "typescript";
            case "py" -> "python";
            case "html" -> "html";
            case "css" -> "css";
            case "json" -> "json";
            case "xml" -> "xml";
            default -> "plaintext";
        };
    }
}
