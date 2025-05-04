package org.example.eventsourcing.Business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.example.eventsourcing.DTOS.EventDTO;
import org.example.eventsourcing.DomainModels.Account;
import org.example.eventsourcing.DomainModels.OrderBook;
import org.example.eventsourcing.DomainModels.Status;
import org.example.eventsourcing.DomainModels.UserHistoryType;
import org.example.eventsourcing.Entities.*;
import org.example.eventsourcing.EventExecutors.EventExecutor;
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
import java.util.Set;

@Service
public class EventService {

    @Autowired
    private File systemLogFile;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventExecutor eventExecutor;


    public List<Map<String, Object>> getAllEvents() {
        try {
            List<Map<String, Object>> events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});

            for (Map<String, Object> event : events) {
                event.compute("eventTime", (k, timestampObj) -> formatDate(timestampObj));
            }

            return events;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read events from log file", e);
        }
    }

    public String addEvent(EventType eventType, EventDTO eventDTO) {
        try {
            List<Map<String,Object>> events;
            if (systemLogFile.length() == 0) {
                events = new ArrayList<>();
            } else {
                events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});
            }

            List<Map<String,Object>> newEvents = new ArrayList<>();

            switch (eventType) {
                case OrderPlaced -> {
                    OrderPlaced orderPlaced = new OrderPlaced(
                            eventDTO.getUserId(),
                            eventDTO.getCompany(),
                            eventDTO.getPrice(),
                            eventDTO.getQuantity(),
                            eventDTO.getSide(),
                            eventDTO.getCurrency()
                    );
                    newEvents.addAll(eventExecutor.handleOrderPlaced(events, orderPlaced));
                }
                case OrderCancelled -> {
                    OrderCancelled orderCancelled = new OrderCancelled(
                            eventDTO.getOrderId(),
                            eventDTO.getCompany(),
                            eventDTO.getUserId()
                    );

                    newEvents.add(objectMapper.convertValue(orderCancelled, new TypeReference<>() {}));
                }

                case FundsCredited -> {
                    FundsCredited fundsCredited = new FundsCredited(
                            eventDTO.getUserId(),
                            eventDTO.getAmount(),
                            eventDTO.getCurrency(),
                            eventDTO.getReference()
                    );
                    newEvents.add(objectMapper.convertValue(fundsCredited, new TypeReference<>() {}));
                }

                case FundsDebited -> {
                    FundsDebited fundsDebited = new FundsDebited(
                            eventDTO.getUserId(),
                            eventDTO.getAmount(),
                            eventDTO.getCurrency(),
                            eventDTO.getReference()
                    );
                    newEvents.add(objectMapper.convertValue(fundsDebited, new TypeReference<>() {}));
                }

                default -> throw new IllegalArgumentException("Unsupported event type: " + eventType);
            }

            events.addAll(newEvents);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(systemLogFile,events);

            return "Event added successfully.";
        } catch (IOException e) {
            throw new RuntimeException("Failed to add event", e);
        }
    }

    public List<Map<String, Object>> getUserEvents(UserHistoryType type,String userId) {
        try {
            List<Map<String, Object>> finalEvents = new ArrayList<>();
            List<Map<String, Object>> events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});

            for (Map<String, Object> event : events) {
                switch (type){
                    case Orders -> {
                        String eventType = event.get("eventType").toString();
                        if((eventType.equals("OrderPlaced") || eventType.equals("OrderCancelled"))){
                            String id = event.get("userId").toString();
                            if(userId.equals(id)){
                                event.compute("eventTime", (k, timestampObj) -> formatDate(timestampObj));
                                finalEvents.add(event);
                            }
                        }
                    }

                    case Funds -> {
                        String eventType = event.get("eventType").toString();
                        if((eventType.equals("FundsCredited") || eventType.equals("FundsDebited"))){
                            String id = event.get("userId").toString();
                            if(userId.equals(id)){
                                event.compute("eventTime", (k, timestampObj) -> formatDate(timestampObj));
                                finalEvents.add(event);
                            }
                        }
                    }

                    case Trades -> {
                        String eventType = event.get("eventType").toString();
                        if((eventType.equals("TradeExecuted"))){
                            String sellId = event.get("sellOrderId").toString();
                            String buyId = event.get("buyOrderId").toString();
                            Pair<String,String> pair = eventExecutor.getUsersIds(sellId,buyId,events);
                            if(userId.equals(pair.getLeft()) || userId.equals(pair.getRight())){
                                event.compute("eventTime", (k, timestampObj) -> formatDate(timestampObj));
                                finalEvents.add(event);
                            }
                        }
                    }

                    default -> throw new IllegalArgumentException("Unsupported event type: " + type);
                }
            }

            return finalEvents;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read events from log file", e);
        }
    }

    public OrderBook getOrderBook() {
        try {
            List<Map<String, Object>> events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});

            return new OrderBook(eventExecutor.getValidOrders(events));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read events from log file", e);
        }
    }

    public Account getUserAccount(String userId) {
        try {
            List<Map<String, Object>> events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});

            return eventExecutor.getAccountFromEvents(events, userId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read events from log file", e);
        }
    }

    public Account modifyUserAccount(BalanceDirection action,EventDTO event) {
        switch (action){
            case DEPOSIT -> {
                addEvent(EventType.FundsCredited, event);
                return getUserAccount(event.getUserId());
            }

            case WITHDRAW -> {
                addEvent(EventType.FundsDebited, event);
                return getUserAccount(event.getUserId());
            }
            default -> throw new IllegalArgumentException("Unsupported event type: " + action);
        }
    }

    public Status getCurrentStatus(){
        try {
            List<Map<String, Object>> events = objectMapper.readValue(systemLogFile, new TypeReference<>() {});

            Set<String> usersIds =  eventExecutor.getUsersIds(events);
            List<Account> accounts = new ArrayList<>();

            for(String userId : usersIds){
                accounts.add(eventExecutor.getAccountFromEvents(events, userId));
            }

            OrderBook orderBook = new OrderBook(eventExecutor.getValidOrders(events));

            List<TradeExecuted> trades = eventExecutor.getTrades(events);

            return new Status(orderBook,accounts,trades);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read events from log file", e);
        }
    }

    private String formatDate(Object timestampObj) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        double timestampDouble = ((Number) timestampObj).doubleValue();
        long seconds = (long) timestampDouble;
        long nanos = (long) ((timestampDouble - seconds) * 1_000_000_000);

        Instant instant = Instant.ofEpochSecond(seconds, nanos);
        return formatter.format(instant);
    }


}
