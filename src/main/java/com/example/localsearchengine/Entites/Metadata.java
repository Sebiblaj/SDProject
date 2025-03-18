package com.example.localsearchengine.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ElementCollection
    @CollectionTable(name = "metadata_values", joinColumns = @JoinColumn(name = "metadata_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> values;

    @Column(name = "file_id")
    private int fileId;
}

