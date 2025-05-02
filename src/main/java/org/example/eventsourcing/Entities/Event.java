package org.example.eventsourcing.Entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Event {

    private UUID eventID;
    private Instant eventTime;
    private EventType eventType;

    public Event(EventType eventType) {
        this.eventType = eventType;
        this.eventTime = Instant.now();
        this.eventID = UUID.randomUUID();
    }
}
