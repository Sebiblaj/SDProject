package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.File;
import com.example.localsearchengine.DTOs.PathAndName;
import com.example.localsearchengine.Entites.FileTags;
import com.example.localsearchengine.Entites.FileType;
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
    List<File> findAllByExtension(String ext);
    @Modifying
    @Query("DELETE FROM File f WHERE (f.path, f.filename) IN :files")
    int deleteByPathAndFilename(@Param("files") List<PathAndName> files);

    @Modifying
    @Query("SELECT f FROM File f WHERE (f.filesize > :min AND f.filesize <= :max )")
    List<File> findBySizeGreaterThan(@Param("min") Integer min, @Param("max") Integer max);

    @Modifying
    @Query("SELECT f.tags FROM File f WHERE f.id = :id")
    List<FileTags> findTagsForFile(String id);

    @Modifying
    @Query("SELECT f.tags FROM File f WHERE f.path = :path AND f.filename = :filename")
    List<FileTags> findTagsForFile(String path, String filename);

    @Modifying
    @Query("SELECT f FROM File f WHERE f.tags = :tag")
    List<File> findFilesForTag(String tag);

    @Modifying
    @Query("SELECT f.id FROM File f WHERE f.path = :path AND f.filename = :filename")
    Integer getFileIdByPathAndFilename(String path, String filename);

}
