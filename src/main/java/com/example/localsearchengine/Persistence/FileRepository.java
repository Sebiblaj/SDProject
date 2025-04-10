package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.DTOs.FileDTOS.PathAndName;
import com.example.localsearchengine.Entites.FileTags;
import com.example.localsearchengine.Entites.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

    List<File> findAllByFilename(String filename);

    File getFileByPathAndFilename(String path, String filename);

    List<File> findAllByExtension(String ext);

    @Modifying
    @Query("DELETE FROM File f WHERE (f.path, f.filename) IN :files")
    int deleteByPathAndFilename(@Param("files") List<PathAndName> files);

    @Modifying
    @Query("DELETE FROM File f WHERE f.path = :path AND f.filename = :filename")
    void deleteByPathAndFilename(@Param("path") String path,@Param("filename") String filename);

    @Query("SELECT f FROM File f WHERE f.filesize > :min AND f.filesize <= :max")
    List<File> findBySizeGreaterThan(@Param("min") String min, @Param("max") String max);

    @Query("SELECT f.tags FROM File f WHERE f.path = :path AND f.filename = :filename")
    List<FileTags> findTagsForFile(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM File f JOIN f.tags t WHERE t.tag = :tag")
    List<File> findFilesForTag(@Param("tag") String tag);

    @Query("SELECT f FROM File f WHERE f.path = :path AND f.filename = :filename")
    File findFilesByPathAndFilename(@Param("path") String path, @Param("filename") String filename);

    @Query("SELECT f FROM File f WHERE f.path IN :paths AND f.filename IN :filenames")
    List<File> findFilesByPathAndFilename(@Param("paths") List<String> paths, @Param("filenames") List<String> filenames);

}

