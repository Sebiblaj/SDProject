package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {

    @Query("SELECT m from Metadata m WHERE m.file.id = :id")
    Metadata getMetadataForFile(String id);

    @Query("SELECT m FROM Metadata m WHERE m.file.id IN :id")
    List<Metadata> getMetadataForFiles(List<String> id);

    @Query("SELECT m from Metadata m WHERE m.file.filename = :filename AND m.file.path = :path")
    Metadata getMetadataForFile(@Param("path") String path, @Param("filename") String filename);

    @Modifying
    @Query("DELETE from Metadata m WHERE m.file.id IN :ids")
    void deleteMetadataForFiles(@Param("ids") List<String> ids);

}
