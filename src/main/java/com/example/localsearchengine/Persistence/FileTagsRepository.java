package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.FileTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTagsRepository extends JpaRepository<FileTags, String> {
}
