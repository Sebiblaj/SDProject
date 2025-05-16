package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entities.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {

    @Query("SELECT m from Metadata m JOIN FileType ft ON m.fileEntity.type.id = ft.id WHERE m.fileEntity.filename = :filename AND m.fileEntity.path = :path AND ft.type = :ext")
    Metadata getMetadataForFile(@Param("path") String path, @Param("filename") String filename,@Param("ext") String ext);

}
