package com.example.localsearchengine.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FileContents {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String contents;

    private String preview;

    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;
}
