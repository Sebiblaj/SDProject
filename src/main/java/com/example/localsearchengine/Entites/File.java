package com.example.localsearchengine.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String path;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private FileType type;

    @Column(nullable = false)
    private int filesize;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Timestamp accessedAt;

    @ElementCollection
    @CollectionTable(name = "file_tags", joinColumns = @JoinColumn(name = "file_id"))
    @Column(name = "tag")
    private String[] tags;
}
