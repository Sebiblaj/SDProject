package com.example.localsearchengine.WidgetsHandler.StrategyHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class DefaultWidgetHandler implements WidgetHandler {


    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public boolean isWidgetValid(String widgetName) {
        return true;
    }

    @Override
    public String getWidgetPage(String widgetName, Context context, Object payload) {
        return templateEngine.process("widgets/" + widgetName, context);
    }
}