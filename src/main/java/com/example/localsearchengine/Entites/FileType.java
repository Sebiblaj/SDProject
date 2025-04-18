package com.example.localsearchengine.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private double weight;

    @Column(nullable = false, unique = true)
    private String type;
}
