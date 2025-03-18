package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, Long> {
}
