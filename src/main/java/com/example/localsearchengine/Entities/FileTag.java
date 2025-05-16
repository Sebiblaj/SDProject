package com.example.localsearchengine.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class FileTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tag;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    @JsonBackReference
    private Set<FileEntity> fileEntities;

    public FileTag() {
    }

    public FileTag(Long id,String tag) {
        this.id = id;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Set<FileEntity> getFileEntities() {
        return fileEntities;
    }

    public void setFileEntities(Set<FileEntity> fileEntities) {
        this.fileEntities = fileEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileTag fileTag = (FileTag) o;
        return id != null && id.equals(fileTag.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "FileTag{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                '}';
    }

}
