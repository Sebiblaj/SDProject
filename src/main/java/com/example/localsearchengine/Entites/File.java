package com.example.localsearchengine.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"filename", "path"}))
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.EAGER)
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

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FileTags> tags;


}
