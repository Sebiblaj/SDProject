package com.example.logger.Persistence;

import com.example.logger.Entities.ActivityType;
import com.example.logger.Entities.QueryType;
import com.example.logger.Entities.Status;
import com.example.logger.Entities.SystemLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoggerRepository extends JpaRepository<SystemLog, String> {

    @Query("""
        SELECT l FROM SystemLog l
        WHERE (:activities IS NULL OR l.activityType IN :activities)
          AND (:queries IS NULL OR l.queryType IN :queries)
          AND (:statuses IS NULL OR l.status IN :statuses)
        ORDER BY l.timestamp DESC
    """)
    List<SystemLog> findLatestLogs(Pageable pageable,
                                   @Param("activities") List<ActivityType> activities,
                                   @Param("queries") List<QueryType> queries,
                                   @Param("statuses") List<Status> statuses);
}
