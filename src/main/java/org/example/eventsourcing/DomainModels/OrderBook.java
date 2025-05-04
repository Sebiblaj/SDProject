package org.example.eventsourcing.DomainModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.eventsourcing.Entities.OrderPlaced;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBook {

    private List<OrderPlaced> orders;
}
