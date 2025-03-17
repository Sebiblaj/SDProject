package com.example.localsearchengine.Persistence;

import com.example.localsearchengine.Entites.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
}
