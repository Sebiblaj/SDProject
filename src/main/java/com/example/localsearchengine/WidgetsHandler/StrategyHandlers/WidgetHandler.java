package com.example.localsearchengine.WidgetsHandler.StrategyHandlers;

import org.thymeleaf.context.Context;

public interface WidgetHandler {

    boolean isWidgetValid(String widgetName);
    String getWidgetPage(String widgetName, Context context, Object payload);
}