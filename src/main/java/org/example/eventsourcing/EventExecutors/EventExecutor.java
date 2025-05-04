package org.example.eventsourcing.EventExecutors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.tuple.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventsourcing.DomainModels.Account;
import org.example.eventsourcing.Entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class EventExecutor {

    @Autowired
    private ObjectMapper objectMapper;

    public List<Map<String,Object>> handleOrderPlaced(List<Map<String,Object>> events, OrderPlaced newOrder) {
        try {
            newOrder.setOrderState(OrderState.PLACED);
            List<Map<String,Object>> newEvents = new ArrayList<>();

            OrderPlaced originalOrder = objectMapper.readValue(objectMapper.writeValueAsString(newOrder), OrderPlaced.class);
            newEvents.add(objectMapper.convertValue(originalOrder, new TypeReference<>() {}));


            String oppositeSide = newOrder.getSide().equals("BUY") ? "SELL" : "BUY";
            List<OrderPlaced> validOrders = getValidOrders(events);

            for (OrderPlaced existingOrder : validOrders) {
                if (!oppositeSide.equals(existingOrder.getSide())) continue;
                if (!newOrder.getCompany().equals(existingOrder.getCompany())) continue;

                boolean priceMatch = newOrder.getSide().equals("BUY")
                        ? newOrder.getPrice() <= existingOrder.getPrice()
                        : newOrder.getPrice() >= existingOrder.getPrice();

                if (!priceMatch) continue;

                int tradeQuantity = Math.min(existingOrder.getQuantity(), newOrder.getQuantity());
                int tradePrice = Math.min(existingOrder.getPrice(), newOrder.getPrice());

                TradeExecuted trade = new TradeExecuted(
                        newOrder.getSide().equals("BUY") ? newOrder.getOrderId() : existingOrder.getOrderId(),
                        newOrder.getSide().equals("SELL") ? newOrder.getOrderId() : existingOrder.getOrderId(),
                        newOrder.getCompany(),
                        tradePrice,
                        tradeQuantity,
                        newOrder.getCurrency()
                );

                FundsCredited fundsCredited = new FundsCredited(
                        newOrder.getSide().equals("SELL") ? newOrder.getUserId() : existingOrder.getUserId(),
                        tradePrice,
                        newOrder.getCurrency(),
                        "STOCK_" + newOrder.getCompany()
                );

                FundsDebited fundsDebited = new FundsDebited(
                        newOrder.getSide().equals("BUY") ? newOrder.getUserId() : existingOrder.getUserId(),
                        tradePrice,
                        newOrder.getCurrency(),
                        "STOCK_" + newOrder.getCompany()
                );

                newEvents.add(objectMapper.convertValue(trade, new TypeReference<>() {}));
                newEvents.add(objectMapper.convertValue(fundsCredited, new TypeReference<>() {}));
                newEvents.add(objectMapper.convertValue(fundsDebited, new TypeReference<>() {}));

                existingOrder.setQuantity(existingOrder.getQuantity() - tradeQuantity);
                existingOrder.setPrice(existingOrder.getPrice() - tradePrice);

                newOrder.setPrice(newOrder.getPrice() - tradePrice);
                newOrder.setQuantity(newOrder.getQuantity() - tradeQuantity);

                existingOrder.setOrderState(
                        existingOrder.getQuantity() == 0 ? OrderState.COMPLETED : OrderState.PLACED);
                newOrder.setOrderState(
                        newOrder.getQuantity() == 0 ? OrderState.COMPLETED : OrderState.PLACED);

                newEvents.add(objectMapper.convertValue(existingOrder, new TypeReference<>() {}));
                newEvents.add(objectMapper.convertValue(newOrder, new TypeReference<>() {}));

                if (newOrder.getQuantity() == 0) break;
            }

            return newEvents;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON processing error", e);
        }
    }

    public Pair<String, String> getUsersIds(String sellId, String buyId, List<Map<String,Object>> events) {
        UUID sellOrderId = UUID.fromString(sellId);
        UUID buyOrderId = UUID.fromString(buyId);

        String sellUserId = null;
        String buyUserId = null;

        for (Object event : events) {
            try {
                String json = objectMapper.writeValueAsString(event);
                JsonNode node = objectMapper.readTree(json);

                if (node.has("eventType") && "OrderPlaced".equals(node.get("eventType").asText())) {
                    OrderPlaced order = objectMapper.treeToValue(node, OrderPlaced.class);

                    if (order.getOrderId().equals(sellOrderId)) {
                        sellUserId = order.getUserId();
                    } else if (order.getOrderId().equals(buyOrderId)) {
                        buyUserId = order.getUserId();
                    }
                }

                if (sellUserId != null && buyUserId != null) {
                    break;
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process event", e);
            }
        }

        if (sellUserId == null || buyUserId == null) {
            throw new RuntimeException("Order not found for given IDs");
        }

        return Pair.of(sellUserId, buyUserId);
    }

    public List<OrderPlaced> getValidOrders(List<Map<String,Object>> events) {
        Map<UUID, OrderPlaced> latestOrders = new HashMap<>();
        Set<UUID> cancelledOrderIds = new HashSet<>();

        for (Object event : events) {
            try {
                String json = objectMapper.writeValueAsString(event);
                JsonNode node = objectMapper.readTree(json);

                if (node.has("eventType")) {
                    String eventType = node.get("eventType").asText();

                    if ("OrderPlaced".equals(eventType)) {
                        OrderPlaced order = objectMapper.treeToValue(node, OrderPlaced.class);
                        latestOrders.put(order.getOrderId(), order);
                    } else if ("OrderCancelled".equals(eventType)) {
                        OrderCancelled cancelled = objectMapper.treeToValue(node, OrderCancelled.class);
                        cancelledOrderIds.add(cancelled.getOrderId());
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process event", e);
            }
        }

        return latestOrders.values().stream()
                .filter(order -> order.getOrderState() != OrderState.COMPLETED)
                .filter(order -> !cancelledOrderIds.contains(order.getOrderId()))
                .collect(Collectors.toList());
    }

    public Account getAccountFromEvents(List<Map<String, Object>> events, String userId) {
        int totalSold = 0;
        int totalBought = 0;

        for (Object event : events) {
            try {
                String json = objectMapper.writeValueAsString(event);
                JsonNode node = objectMapper.readTree(json);

                if (node.has("eventType")) {
                    String eventType = node.get("eventType").asText();

                    if ("FundsCredited".equals(eventType)) {
                        FundsCredited credited = objectMapper.treeToValue(node, FundsCredited.class);
                        if (userId.equals(credited.getUserId())) {
                            totalSold += credited.getAmount();
                        }
                    } else if ("FundsDebited".equals(eventType)) {
                        FundsDebited debited = objectMapper.treeToValue(node, FundsDebited.class);
                        if (userId.equals(debited.getUserId())) {
                            totalBought += debited.getAmount();
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process event", e);
            }
        }

        int currentSold = totalSold - totalBought;
        return new Account(userId, totalBought, totalSold, currentSold);
    }

    public Set<String> getUsersIds(List<Map<String,Object>> events){

        Set<String> usersIds = new HashSet<>();

        for (Object event : events) {
            try {
                String json = objectMapper.writeValueAsString(event);
                JsonNode node = objectMapper.readTree(json);

                if (node.has("eventType")) {
                    String eventType = node.get("eventType").asText();

                    switch (eventType){
                        case "OrderPlaced" -> {
                            OrderPlaced order = objectMapper.treeToValue(node, OrderPlaced.class);
                            usersIds.add(order.getUserId());
                        }
                        case "OrderCancelled" -> {
                            OrderCancelled cancelled = objectMapper.treeToValue(node, OrderCancelled.class);
                            usersIds.add(cancelled.getUserId());
                        }
                        case "FundsCredited" -> {
                            FundsCredited credited = objectMapper.treeToValue(node, FundsCredited.class);
                            usersIds.add(credited.getUserId());
                        }
                        case "FundsDebited" -> {
                            FundsDebited debited = objectMapper.treeToValue(node, FundsDebited.class);
                            usersIds.add(debited.getUserId());
                        }
                        case "TradeExecuted" -> { }

                        default -> {
                            throw new IllegalArgumentException("Unsupported event type: " + eventType);
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process event", e);
            }
        }

        return usersIds;
    }

    public List<TradeExecuted> getTrades(List<Map<String,Object>> events){

        List<TradeExecuted> trades = new ArrayList<>();

        for (Object event : events) {
            try {
                String json = objectMapper.writeValueAsString(event);
                JsonNode node = objectMapper.readTree(json);

                if (node.has("eventType")) {
                    String eventType = node.get("eventType").asText();

                    if ("TradeExecuted".equals(eventType)) {
                        TradeExecuted tradeExecuted = objectMapper.treeToValue(node, TradeExecuted.class);
                        trades.add(tradeExecuted);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process event", e);
            }
        }

        return trades;
    }
}