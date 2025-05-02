package org.example.eventsourcing.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {

    private UUID orderId;
    private String userId;
    private Integer price;
    private Integer quantity;
    private String side;
    private Integer amount;
    private String reference;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private String currency;
}
