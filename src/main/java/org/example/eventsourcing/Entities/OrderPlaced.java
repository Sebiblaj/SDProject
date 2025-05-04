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
    private String company;
    private Integer price;
    private Integer quantity;
    private String currency;
    private String side;
    private OrderState orderState;

    public OrderPlaced(String userId,String company, Integer price, Integer quantity, String side,String currency) {
        super(EventType.OrderPlaced);
        this.orderId = UUID.randomUUID();
        this.company = company;
        this.userId = userId;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.currency = currency;
    }
}
