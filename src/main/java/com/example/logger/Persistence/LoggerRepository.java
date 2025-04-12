package com.example.logger.Persistence;

import com.example.logger.Entities.ActivityType;
import com.example.logger.Entities.QueryType;
import com.example.logger.Entities.Status;
import com.example.logger.Entities.SystemLog;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoggerRepository extends JpaRepository<SystemLog,String> {

    @Query("SELECT l FROM SystemLog l WHERE l.activityType = :activity ORDER BY l.timestamp DESC")
    List<SystemLog> findLatestLogs(PageRequest pageable, @Param("activity") ActivityType activity);

    @Query("SELECT l FROM SystemLog l WHERE l.queryType = :query ORDER BY l.timestamp DESC")
    List<SystemLog> findLatestLogs(PageRequest pageable, @Param("query")QueryType queryType);

    @Query("SELECT l FROM SystemLog l WHERE l.status = :status ORDER BY l.timestamp DESC")
    List<SystemLog> findLatestLogs(PageRequest pageable, @Param("status") Status status);
}
