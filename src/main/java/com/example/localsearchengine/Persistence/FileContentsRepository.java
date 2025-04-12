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

    @Query(value = "SELECT * FROM file_contents f WHERE f.path = :path AND f.search_vector @@ to_tsquery('english', :keyword)", nativeQuery = true)
    List<FileContents> getFileContentsByPath(@Param("path") String path, @Param("keyword") String keyword);

    @Query(value = "SELECT * FROM file_contents f WHERE f.filename = :filename AND f.search_vector @@ to_tsquery('english', :keyword)", nativeQuery = true)
    List<FileContents> getFileContentsByName(@Param("filename") String filename, @Param("keyword") String keyword);

    @Query(value = "SELECT * FROM file_contents f WHERE f.search_vector @@ to_tsquery('english', :keyword)", nativeQuery = true)
    List<FileContents> searchFileByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT fc.* FROM file_contents fc " +
            "JOIN file f ON fc.file_id = f.id " +
            "WHERE f.filename = :filename " +
            "AND f.path = :path " +
            "AND fc.search_vector @@ to_tsquery('english', :keyword)",
            nativeQuery = true)
    FileContents searchFileByKeyword(@Param("keyword") String keyword, @Param("path") String path, @Param("filename") String filename);




    FileContents findByFile(File file);

}

