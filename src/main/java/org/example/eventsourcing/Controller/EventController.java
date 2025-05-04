package org.example.eventsourcing.Controller;

import org.example.eventsourcing.Business.EventService;
import org.example.eventsourcing.DTOS.EventDTO;
import org.example.eventsourcing.DomainModels.Account;
import org.example.eventsourcing.DomainModels.OrderBook;
import org.example.eventsourcing.DomainModels.Status;
import org.example.eventsourcing.DomainModels.UserHistoryType;
import org.example.eventsourcing.Entities.BalanceDirection;
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

    @GetMapping(value = "orderBook")
    public ResponseEntity<OrderBook> getOrderBook() {
        return ResponseEntity.ok(eventService.getOrderBook());
    }

    @GetMapping(value = "account")
    public ResponseEntity<Account> getUserAccount(@RequestParam String userId) {
        return ResponseEntity.ok(eventService.getUserAccount(userId));
    }

    @PostMapping(value = "account")
    public ResponseEntity<Account> modifyBalance(@RequestParam BalanceDirection action, @RequestBody EventDTO event) {
        return ResponseEntity.ok(eventService.modifyUserAccount(action, event));
    }

    @GetMapping(value = "userHistory")
    public ResponseEntity<List<Map<String,Object>>> getHistory(@RequestParam UserHistoryType type,
                                                               @RequestParam String userId) {
        List<Map<String,Object>> history = eventService.getUserEvents(type, userId);
        return history.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(history);
    }

    @GetMapping(value = "status")
    public ResponseEntity<Status> getStatus() {
        return ResponseEntity.ok(eventService.getCurrentStatus());
    }
}
