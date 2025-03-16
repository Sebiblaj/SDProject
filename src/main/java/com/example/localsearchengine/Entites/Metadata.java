package com.example.localsearchengine.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String key;

    @ElementCollection
    @CollectionTable(name = "metadata_values", joinColumns = @JoinColumn(name = "metadata_id"))
    @Column(name = "value")
    private String[] value;

    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;
}
