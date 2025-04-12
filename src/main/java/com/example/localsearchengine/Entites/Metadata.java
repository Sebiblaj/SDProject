package com.example.localsearchengine.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "metadata_values", joinColumns = @JoinColumn(name = "metadata_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> values;


    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_file_metadata", value = ConstraintMode.CONSTRAINT))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File file;

}

