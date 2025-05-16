package com.example.localsearchengine.WidgetsHandler;

import com.example.localsearchengine.DTOs.FileSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("widgets")
public class WidgetsController {

    @Autowired
    private WidgetsService widgetsService;

    @PostMapping("/{widgetName}")
    public ResponseEntity<String> getWidgetPage(@PathVariable String widgetName,
                                                @RequestBody(required = false) FileSearchCriteria fileSearchCriteria) {

        String widget = widgetsService.returnWidget(widgetName,fileSearchCriteria);
        return widget != null ? ResponseEntity.ok(widget) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{widgetName}")
    public ResponseEntity<String> getWidgetPage(@PathVariable String widgetName) {
        String widget = widgetsService.returnWidget(widgetName,new FileSearchCriteria());
        return widget != null ? ResponseEntity.ok(widget) : ResponseEntity.notFound().build();
    }
}
