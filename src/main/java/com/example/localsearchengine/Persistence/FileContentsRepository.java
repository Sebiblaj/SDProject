package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entities.FileEntity;
import com.example.localsearchengine.Entities.FileContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileContentsRepository extends JpaRepository<FileContents, String>, JpaSpecificationExecutor<FileContents> {

    @Query("SELECT f FROM FileContents f " +
            "WHERE f.fileEntity.filename = :filename " +
            "AND f.fileEntity.path = :path " +
            "AND f.fileEntity.type.type = :ext")
    FileContents getFileContentsByPathAndFilename(@Param("path") String path, @Param("filename") String filename, @Param("ext") String ext);

    FileContents findByFileEntity(FileEntity fileEntity);
}
