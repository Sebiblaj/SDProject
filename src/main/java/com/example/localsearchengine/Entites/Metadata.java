package com.example.localsearchengine.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String key;

    @OneToMany(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetadataValue> values;

    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;
}
