package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileContentsRepository extends JpaRepository<FileContents, String> {

    @Query("SELECT f FROM FileContents f WHERE f.file.filename = :filename AND f.file.path = :path")
    FileContents getFileContentsByPathAndFilename(String path, String filename);

    @Query(value = "SELECT * FROM file_contents f WHERE f.search_vector @@ to_tsquery('english', :keyword) LIMIT 10", nativeQuery = true)
    List<FileContents> searchFilesByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM file_contents f WHERE f.file_id = (SELECT id FROM file WHERE path = :path AND filename = :filename) AND f.search_vector @@ to_tsquery('english', :keyword)", nativeQuery = true)
    FileContents searchFilesByKeyword(@Param("keyword") String keyword, @Param("path") String path, @Param("filename") String filename);

    FileContents findByFile(File file);

    @Query("SELECT f.preview FROM FileContents f WHERE f.file.filename = :filename AND f.file.path = :filepath")
    String findPreviewByPathAndFilename(@Param("filename") String filename, @Param("filepath") String filepath);


}

