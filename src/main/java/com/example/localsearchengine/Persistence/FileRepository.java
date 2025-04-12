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

@Repository
public interface FileRepository extends JpaRepository<File, String> {

    List<File> findAllByFilename(String filename);

    File getFileByPathAndFilename(String path, String filename);

    @Query("SELECT f FROM File f WHERE f.type.type = :ext")
    List<File> findAllByExtension(@Param("ext") String ext);

    @Modifying
    @Query("DELETE FROM File f WHERE (f.path, f.filename) IN :files")
    int deleteByPathAndFilename(@Param("files") List<PathAndName> files);

    @Modifying
    @Query("DELETE FROM File f WHERE f.path = :path AND f.filename = :filename")
    void deleteByPathAndFilename(@Param("path") String path,@Param("filename") String filename);

    @Query(value = """
    SELECT f.* FROM file f JOIN metadata m ON f.id = m.file_id JOIN metadata_values mv ON mv.metadata_id = m.id
    WHERE mv.key = 'filesize' AND CAST(mv.value AS BIGINT) BETWEEN :min AND :max""", nativeQuery = true)
    List<File> findByFileSizeBetween(@Param("min") long min, @Param("max") long max);


    @Query("SELECT f.tags FROM File f WHERE f.path = :path AND f.filename = :filename")
    List<FileTag> findTagsForFile(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM File f JOIN f.tags t WHERE t.tag = :tag")
    List<File> findFilesForTag(@Param("tag") String tag);

    @Query("SELECT f FROM File f WHERE f.path = :path AND f.filename = :filename")
    File findFilesByPathAndFilename(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM File f WHERE f.path IN :paths AND f.filename IN :filenames")
    List<File> findFilesByPathAndFilename(@Param("paths") List<String> paths, @Param("filenames") List<String> filenames);

}

