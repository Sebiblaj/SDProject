package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {

    @Query("SELECT m from Metadata m WHERE m.fileId = :id")
    Metadata getMetadataForFile(String id);

    @Query("SELECT m FROM Metadata m WHERE m.fileId IN :id")
    List<Metadata> getMetadataForFiles(List<String> id);

}
