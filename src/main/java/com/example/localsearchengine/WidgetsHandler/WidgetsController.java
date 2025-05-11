package com.example.localsearchengine.WidgetsHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("widgets")
public class WidgetsController {

     @Autowired
     private WidgetsRegistry widgetsRegistry;


    @GetMapping("/{widgetName}")
    public String getWidgetPage(@PathVariable String widgetName) {
        String widget = widgetsRegistry.hasWidget(widgetName);
        if (widget != null) {
            return "widgets/" + widget + ".html" ;
        } else {
            return "error/404";
        }
    }

}
