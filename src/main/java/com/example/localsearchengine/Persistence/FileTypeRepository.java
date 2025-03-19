package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, Long> {

    @Query("SELECT COUNT(f) > 0 FROM FileType f WHERE f.type = :fileType")
    Boolean existsByFileType(@Param("fileType") String fileType);

    @Query("SELECT f  FROM FileType f WHERE f.type = :fileType")
    FileType getFileTypeByType(@Param("fileType") String fileType);

}
