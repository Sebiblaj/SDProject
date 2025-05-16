package com.example.localsearchengine.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "file_entity", uniqueConstraints = @UniqueConstraint(columnNames = {"filename", "path","type_id"}))
public class FileEntity {

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "file_tags",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonManagedReference
    private Set<FileTag> tags;

    @OneToOne(mappedBy = "fileEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Metadata metadata;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity fileEntity = (FileEntity) o;
        return id == fileEntity.id;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                '}';
    }

    public FileEntity() {
    }

    public FileEntity(String filename, String path, FileType type) {
        this.filename = filename;
        this.path = path;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public FileType getType() {
        return type;
    }

    public Set<FileTag> getTags() {
        return tags;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public void setTags(Set<FileTag> tags) {
        this.tags = tags;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

}
