package com.example.localsearchengine.Entities;

import jakarta.persistence.*;


@Entity
public class FileType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private double weight;

    @Column(nullable = false, unique = true)
    private String type;

    public FileType(String type, double weight) {
        this.type = type;
        this.weight = weight;
    }

    public FileType() {
    }

    public Integer getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }
    public String getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileType{" +
                "id=" + id +
                ", weight=" + weight +
                ", type='" + type + '\'' +
                '}';
    }
}
