package org.example.eventsourcing.Controller;

import org.example.eventsourcing.Business.EventService;
import org.example.eventsourcing.DTOS.EventDTO;
import org.example.eventsourcing.Entities.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping(value = "events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping()
    public ResponseEntity<List<Map<String,Object>>> getEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping(value = "add")
    public ResponseEntity<String> addEvent(@RequestParam EventType type, @RequestBody EventDTO event) {
        return ResponseEntity.ok(eventService.addEvent(type, event));
    }
}
