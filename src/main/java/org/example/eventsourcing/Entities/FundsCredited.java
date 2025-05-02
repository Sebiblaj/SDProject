package org.example.eventsourcing.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FundsCredited extends Event {
    private String userId;
    private Integer amount;
    private String currency;
    private String reference;

    public FundsCredited(String userId, Integer amount,
                         String currency, String reference) {
        super(EventType.FundsCredited);
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.reference = reference;
    }

}
