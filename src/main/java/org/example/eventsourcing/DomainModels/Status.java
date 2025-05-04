package org.example.eventsourcing.DomainModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.eventsourcing.Entities.TradeExecuted;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {

    private OrderBook orderBook;

    private List<Account> accounts;

    private List<TradeExecuted> trades;

}
