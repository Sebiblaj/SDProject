package org.example.eventsourcing.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TradeExecuted extends Event {
    private UUID tradeId;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private Integer price;
    private Integer quantity;
    private String currency;

    public TradeExecuted(UUID buyOrderId, UUID sellOrderId,
                          Integer price, Integer quantity , String currency) {
        super(EventType.TradeExecuted);
        this.tradeId = UUID.randomUUID();
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
        this.currency = currency;
    }

}
