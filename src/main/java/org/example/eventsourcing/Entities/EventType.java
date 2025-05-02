package org.example.eventsourcing.Entities;

public enum EventType {
    OrderPlaced,
    OrderCancelled,
    TradeExecuted,
    FundsDebited,
    FundsCredited
}
