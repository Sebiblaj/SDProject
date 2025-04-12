package com.example.localsearchengine.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "file_contents",
        indexes = @Index(name = "idx_search_vector", columnList = "search_vector")
)
public class FileContents {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(columnDefinition = "TEXT")
    private String preview;

    @Column(columnDefinition = "tsvector")
    @ColumnTransformer(read = "search_vector::text", write = "?::tsvector")
    private String searchVector;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_file_contents", value = ConstraintMode.CONSTRAINT))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File file;

}




