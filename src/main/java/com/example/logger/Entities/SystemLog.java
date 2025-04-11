package com.example.logger.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Timestamp timestamp;
    private String fileName;
    private String filePath;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    private QueryType queryType;
}
