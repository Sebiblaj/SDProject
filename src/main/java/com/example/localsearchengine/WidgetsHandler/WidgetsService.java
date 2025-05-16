package com.example.localsearchengine.WidgetsHandler;

import com.example.localsearchengine.DTOs.FileSearchCriteria;
import com.example.localsearchengine.WidgetsHandler.StrategyHandlers.DefaultWidgetHandler;
import com.example.localsearchengine.WidgetsHandler.StrategyHandlers.WidgetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class WidgetsService {

    @Autowired
    private WidgetsRegistry widgetsRegistry;

    @Autowired
    private List<WidgetHandler> widgetHandlers;

    public String returnWidget(String widgetName, FileSearchCriteria fileSearchCriteria) {
        Context context = new Context();

        widgetHandlers.sort((h1, h2) -> {
            if (h1 instanceof DefaultWidgetHandler) return 1;
            if (h2 instanceof DefaultWidgetHandler) return -1;
            return 0;
        });

        String widget = widgetsRegistry.hasWidget(widgetName);

        for (WidgetHandler handler : widgetHandlers) {
            if (handler.isWidgetValid(widgetName)) {
                return handler.getWidgetPage(widget, context, fileSearchCriteria);
            }
        }
        throw new IllegalArgumentException("Unknown widget: " + widgetName);
    }

}