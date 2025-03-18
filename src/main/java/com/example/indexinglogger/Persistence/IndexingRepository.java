package com.example.indexinglogger.Persistence;

import com.example.indexinglogger.Entities.IndexingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexingRepository extends JpaRepository<IndexingLog,String> {
}
