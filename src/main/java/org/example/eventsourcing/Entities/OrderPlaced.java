package org.example.eventsourcing.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor()
public class OrderPlaced extends Event {
    private UUID orderId;
    private String userId;
    private Integer price;
    private Integer quantity;
    private String currency;
    private String side;

    public OrderPlaced(String userId, Integer price, Integer quantity, String side,String currency) {
        super(EventType.OrderPlaced);
        this.orderId = UUID.randomUUID();
        this.userId = userId;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.currency = currency;
    }
}
