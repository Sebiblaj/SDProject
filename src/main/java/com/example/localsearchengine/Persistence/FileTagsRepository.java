package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.FileTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTagsRepository extends JpaRepository<FileTag, String> {
    FileTag findByTag(String tag);
}
