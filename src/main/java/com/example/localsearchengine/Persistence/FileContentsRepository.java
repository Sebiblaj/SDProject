package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.FileContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileContentsRepository extends JpaRepository<FileContents, String> {

    @Modifying
    @Query("SELECT f FROM FileContents f WHERE (f.file.filename = :filename AND f.file.path = :path)")
    FileContents getFileContentsByPathAndFilename(String path,String filename);
}
