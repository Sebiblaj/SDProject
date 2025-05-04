package org.example.eventsourcing.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OrderCancelled extends Event {

    private UUID orderId;
    private String company;
    private String userId;

    public OrderCancelled(UUID orderId,String company, String userId) {
        super(EventType.OrderCancelled);
        this.company = company;
        this.orderId = orderId;
        this.userId = userId;
    }
}
