package com.example.localsearchengine.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileContents {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String contents;

    private String preview;

    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;

    public void setContents(String contents) {
        this.contents = contents;
        this.preview = generatePreview(contents);
    }

    private String generatePreview(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String[] lines = text.split("\n");
        int previewLines = Math.min(3, lines.length);
        return String.join("\n", java.util.Arrays.copyOfRange(lines, 0, previewLines));
    }
}

