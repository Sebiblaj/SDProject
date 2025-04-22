package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.DTOs.FileDTOS.PathAndName;
import com.example.localsearchengine.Entites.FileTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

    @Query("SELECT f FROM File f " +
            "JOIN Metadata m ON f.id = m.file.id " +
            "WHERE f.filename LIKE :filename " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<File> findAllByFilename(@Param("filename") String filename);


    @Query("SELECT f FROM File f " +
            "JOIN Metadata m ON f.id = m.file.id " +
            "WHERE KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<File> findAllFiles();

    File getFileByPathAndFilename(String path, String filename);

    @Query("SELECT f FROM File f " +
            "JOIN Metadata m ON f.id = m.file.id " +
            "WHERE f.type.type = :ext " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<File> findAllByExtension(@Param("ext") String ext);


    @Modifying
    @Query("DELETE FROM File f WHERE (f.path, f.filename) IN :files")
    int deleteByPathAndFilename(@Param("files") List<PathAndName> files);

    @Query(value = """
    SELECT f.* FROM file f
    JOIN metadata m ON f.id = m.file_id
    JOIN metadata_values mv ON mv.metadata_id = m.id
    WHERE mv.key = 'filesize'
      AND CAST(mv.value AS BIGINT) BETWEEN :min AND :max
    ORDER BY CAST((SELECT value FROM metadata_values WHERE metadata_id = m.id AND key = 'weight') AS FLOAT) DESC
    """, nativeQuery = true)
    List<File> findByFileSizeBetween(@Param("min") long min, @Param("max") long max);

    @Query("SELECT f.tags FROM File f WHERE f.path = :path AND f.filename = :filename")
    List<FileTag> findTagsForFile(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM File f " +
            "JOIN f.tags t " +
            "JOIN Metadata m ON f.id = m.file.id " +
            "WHERE t.tag = :tag " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<File> findFilesForTag(@Param("tag") String tag);


    @Query("SELECT f FROM File f " +
            "JOIN Metadata m ON f.id = m.file.id " +
            "WHERE f.path = :path AND f.filename = :filename " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    File findFilesByPathAndFilename(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM File f " +
            "JOIN Metadata m ON f.id = m.file.id " +
            "WHERE f.path IN :paths AND f.filename IN :filenames " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<File> findFilesByPathAndFilename(@Param("paths") List<String> paths, @Param("filenames") List<String> filenames);

    List<File> findFilesByTags(Set<FileTag> tags);
}


