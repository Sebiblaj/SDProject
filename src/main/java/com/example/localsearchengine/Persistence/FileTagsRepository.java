package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entities.FileTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FileTagsRepository extends JpaRepository<FileTag, String> {

    @Query("SELECT ft FROM FileTag ft " +
            "JOIN ft.fileEntities f " +
            "JOIN Metadata m ON m.fileEntity.id = f.id " +
            "WHERE ft.tag LIKE %:tag% " +
            "AND KEY(m.values) = 'weight' " +
            "ORDER BY CAST(m.values['weight'] AS float) DESC")
    Set<FileTag> findByTags(@Param("tag") String tag);

    FileTag findByTag(String tag);

}
