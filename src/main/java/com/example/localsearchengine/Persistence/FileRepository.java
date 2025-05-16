package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entities.FileEntity;
import com.example.localsearchengine.DTOs.FileDTOS.PathAndName;
import com.example.localsearchengine.Entities.FileTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, String> {

    @Query("SELECT f FROM FileEntity f " +
            "JOIN Metadata m ON f.id = m.fileEntity.id " +
            "WHERE f.filename LIKE :filename " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<FileEntity> findAllByFilename(@Param("filename") String filename);


    @Query("SELECT f FROM FileEntity f " +
            "JOIN Metadata m ON f.id = m.fileEntity.id " +
            "WHERE KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<FileEntity> findAllFiles();

    @Query("SELECT f FROM FileEntity f JOIN FileType ft ON f.type.id = ft.id WHERE " +
            "f.filename = :filename AND f.path = :path AND ft.type = :ext")
    FileEntity getFileByPathAndFilenameAndExtension(@Param("path") String path, @Param("filename") String filename, @Param("ext") String ext);

    @Query("SELECT f FROM FileEntity f " +
            "JOIN Metadata m ON f.id = m.fileEntity.id " +
            "WHERE f.type.type = :ext " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<FileEntity> findAllByExtension(@Param("ext") String ext);


    @Modifying
    @Query("DELETE FROM FileEntity f WHERE (f.path, f.filename) IN :files")
    int deleteByPathAndFilename(@Param("files") List<PathAndName> files);

    @Query(value = """
    SELECT f.* FROM fileEntity f
    JOIN metadata m ON f.id = m.file_id
    JOIN metadata_values mv ON mv.metadata_id = m.id
    WHERE mv.key = 'filesize'
      AND CAST(mv.value AS BIGINT) BETWEEN :min AND :max
    ORDER BY CAST((SELECT value FROM metadata_values WHERE metadata_id = m.id AND key = 'weight') AS FLOAT) DESC
    """, nativeQuery = true)
    List<FileEntity> findByFileSizeBetween(@Param("min") long min, @Param("max") long max);

    @Query("SELECT f.tags FROM FileEntity f WHERE f.path = :path AND f.filename = :filename")
    List<FileTag> findTagsForFile(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM FileEntity f " +
            "JOIN Metadata m ON f.id = m.fileEntity.id " +
            "WHERE f.path = :path AND f.filename = :filename " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    FileEntity findFilesByPathAndFilename(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM FileEntity f " +
            "JOIN Metadata m ON f.id = m.fileEntity.id " +
            "WHERE f.path IN :path AND f.filename IN :filename " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<FileEntity> findFilesByPathAndFilename(@Param("path") List<String> path, @Param("filename") List<String> filename);

    @Query("SELECT f FROM FileEntity f " +
            "JOIN Metadata m ON f.id = m.fileEntity.id " +
            "JOIN FileType ft ON f.type.id = ft.id " +
            "WHERE f.path IN :paths AND f.filename IN :filenames AND ft.type IN :ext " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    List<FileEntity> findFilesByPathAndFilenameAndExtension(
            @Param("paths") List<String> paths,
            @Param("filenames") List<String> filenames,
            @Param("ext") List<String> ext);
    List<FileEntity> findFilesByTags(Set<FileTag> tags);
}
