package com.example.localsearchengine.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Map;

@Entity
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
    private FileEntity fileEntity;

    public Metadata() {
    }

    public Metadata(Integer id,Map<String, String> values,FileEntity fileEntity) {
        this.id = id;
        this.values = values;
        this.fileEntity = fileEntity;
    }

    public Integer getId() {
        return id;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public FileEntity getFileEntity() {
        return fileEntity;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public void setFileEntity(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "id=" + id +
                ", values=" + values +
                '}';
    }

    public FileEntity getFile() {
        return fileEntity;
    }

    public void setFile(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }




}
