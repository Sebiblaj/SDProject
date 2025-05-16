package com.example.localsearchengine.WidgetsHandler.StrategyHandlers;

import com.example.localsearchengine.DTOs.FileSearchCriteria;
import com.example.localsearchengine.Services.FileContentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;

@Component
public class GalleryWidgetStrategy implements WidgetHandler {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private FileContentsService fileContentsService;

    private static final String GALLERY_WIDGET_TEMPLATE = "gallery";
    @Override
    public boolean isWidgetValid(String widgetName) {
        return widgetName.equals(GALLERY_WIDGET_TEMPLATE);
    }

    @Override
    public String getWidgetPage(String widgetName, Context context, Object payload) {
        if(payload instanceof FileSearchCriteria fileSearchCriteria){
            List<String> imageContents = new ArrayList<>();

            List<String> filePath = fileSearchCriteria.getPaths();
            List<String> fileName = fileSearchCriteria.getNames();
            List<String> extension = fileSearchCriteria.getKeywords();

            for (int i = 0; i < fileSearchCriteria.getPaths().size(); i++) {
                String fullContent = fileContentsService.getFileContents(filePath.get(i), fileName.get(i), extension.get(i));
                String base64Content = extractBase64(fullContent);
                String base64Image = "data:image/bmp;base64," + base64Content;
                imageContents.add(base64Image);
            }

            context.setVariable("images", imageContents);

            return templateEngine.process("widgets/" + widgetName, context);
        }
        return null;
    }

    private String extractBase64(String rawContent) {
        String marker = "--- BASE64 IMAGE CONTENT ---";
        int index = rawContent.indexOf(marker);
        if (index != -1) {
            return rawContent.substring(index + marker.length()).trim();
        } else {
            return rawContent.trim();
        }
    }
}