package com.example.logger.Persistence;

import com.example.logger.Entities.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepository extends JpaRepository<SystemLog,String> {
}
