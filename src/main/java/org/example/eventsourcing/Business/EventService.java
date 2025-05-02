package org.example.eventsourcing.Business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventsourcing.DTOS.EventDTO;
import org.example.eventsourcing.Entities.EventType;
import org.example.eventsourcing.Entities.OrderPlaced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    @Autowired
    private File systemLogFile;

    @Autowired
    private ObjectMapper objectMapper;


    public List<Map<String, Object>> getAllEvents() {
        try {
            List<Map<String, Object>> events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

            for (Map<String, Object> event : events) {
                Object timestampObj = event.get("eventTime");

                if (timestampObj instanceof Number) {
                    double timestampDouble = ((Number) timestampObj).doubleValue();
                    long seconds = (long) timestampDouble;
                    long nanos = (long) ((timestampDouble - seconds) * 1_000_000_000);

                    Instant instant = Instant.ofEpochSecond(seconds, nanos);
                    String formattedTime = formatter.format(instant);

                    event.put("eventTime", formattedTime);
                }
            }

            return events;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read events from log file", e);
        }
    }




    public String addEvent(EventType eventType, EventDTO eventDTO) {
        try {
            List<Object> events;
            if (systemLogFile.length() == 0) {
                events = new ArrayList<>();
            } else {
                events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});
            }

            switch (eventType) {
                case OrderPlaced -> {
                    OrderPlaced orderPlaced = new OrderPlaced(
                            eventDTO.getUserId(),
                            eventDTO.getPrice(),
                            eventDTO.getQuantity(),
                            eventDTO.getSide() ,
                            eventDTO.getCurrency()
                    );
                    events.add(orderPlaced);
                }
                default -> throw new IllegalArgumentException("Unsupported event type: " + eventType);
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(systemLogFile, events);

            return "Event added successfully.";
        } catch (IOException e) {
            throw new RuntimeException("Failed to add event", e);
        }
    }
}
