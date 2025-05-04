package org.example.eventsourcing.DomainModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String userId;
    private int totalBought;
    private int totalSold;
    private int netPosition;
}

