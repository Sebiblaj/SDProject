package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.Entites.FileContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileContentsRepository extends JpaRepository<FileContents, String>, JpaSpecificationExecutor<FileContents> {

    @Query("SELECT f FROM FileContents f " +
            "WHERE f.file.filename = :filename " +
            "AND f.file.path = :path " +
            "AND f.file.type.type = :ext")
    FileContents getFileContentsByPathAndFilename(@Param("path") String path, @Param("filename") String filename, @Param("ext") String ext);

    @Query(value = """
    SELECT fc.* 
    FROM file_contents fc
    JOIN file f ON fc.file_id = f.id
    JOIN metadata m ON f.id = m.file_id
    JOIN metadata_values mv ON mv.metadata_id = m.id AND mv.key = 'weight'
    WHERE f.path = :filepath 
      AND fc.search_vector @@ to_tsquery('english', :keyword || ':*')
    ORDER BY mv.value::float DESC
    """, nativeQuery = true)
    List<FileContents> getFileContentsByPath(@Param("filepath") String path, @Param("keyword") String keyword);

    @Query(value = """
    SELECT fc.* 
    FROM file_contents fc
    JOIN file f ON fc.file_id = f.id
    JOIN metadata m ON f.id = m.file_id
    JOIN metadata_values mv ON mv.metadata_id = m.id AND mv.key = 'weight'
    WHERE f.filename LIKE %:filename% 
      AND fc.search_vector @@ to_tsquery('english', :keyword || ':*')  
    ORDER BY mv.value::float DESC
    """, nativeQuery = true)
    List<FileContents> getFileContentsByName(@Param("filename") String filename, @Param("keyword") String keyword);

    @Query(value = """
    SELECT fc.* 
    FROM file_contents fc
    JOIN file f ON fc.file_id = f.id
    JOIN metadata m ON f.id = m.file_id
    JOIN metadata_values mv ON mv.metadata_id = m.id AND mv.key = 'weight'
    WHERE fc.search_vector @@ to_tsquery('english', :keyword || ':*')
    ORDER BY mv.value::float DESC
    """, nativeQuery = true)
    List<FileContents> searchFileByKeyword(@Param("keyword") String keyword);

    @Query(value = """
    SELECT fc.* 
    FROM file_contents fc
    JOIN file f ON fc.file_id = f.id
    JOIN metadata m ON f.id = m.file_id
    JOIN metadata_values mv ON mv.metadata_id = m.id AND mv.key = 'weight'
    WHERE f.filename LIKE %:filename% 
      AND f.path = :filepath
      AND fc.search_vector @@ to_tsquery('english', :keyword || ':*') 
      AND mv.key = 'weight'
    ORDER BY mv.value::float DESC
    """, nativeQuery = true)
    List<FileContents> searchFileByKeyword(
            @Param("filepath") String path,
            @Param("filename") String filename,
            @Param("keyword") String keyword);

    FileContents findByFile(@Param("file") File file);
}
