package org.example.eventsourcing.DomainModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.eventsourcing.Entities.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHistory {

    private List<OrderPlaced> ordersPlaced;
    private List<OrderCancelled> ordersCancelled;
    private List<TradeExecuted> tradesExecuted;
    private List<FundsCredited> fundsCredited;
    private List<FundsDebited> fundsDebited;

}
