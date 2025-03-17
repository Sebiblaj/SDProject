package com.example.localsearchengine.Entites;

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

    private Timestamp timestamp;

    private String status;

    private String message;

    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;
}
