package com.example.localsearchengine.Entities;


import jakarta.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
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


    @Column(name = "search_vector", insertable = false, updatable = false)
    private String searchVector;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_file_contents", value = ConstraintMode.CONSTRAINT))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FileEntity fileEntity;

    public FileContents() {
    }

    public FileContents(String contents, String preview) {
        this.contents = contents;
        this.preview = preview;
    }

    public int getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }
    public String getPreview() {
        return preview;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getSearchVector() {
        return searchVector;
    }

    public void setSearchVector(String searchVector) {
        this.searchVector = searchVector;
    }

    public FileEntity getFileEntity() {
        return fileEntity;
    }

    public void setFileEntity(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    public FileEntity getFile() {
        return fileEntity;
    }

    public void setFile(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    @Override
    public String toString() {
        return "FileContents{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", preview='" + preview + '\'' +
                '}';
    }
}
