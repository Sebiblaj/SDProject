package com.example.indexinglogger.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class IndexingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String fileId;

    private Timestamp timestamp;

    private String status;

    private String message;

}

