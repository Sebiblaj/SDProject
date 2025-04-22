package com.example.logger.Service;

import com.example.logger.DTOS.SystemLogDTO;
import com.example.logger.Entities.ActivityType;
import com.example.logger.Entities.QueryType;
import com.example.logger.Entities.Status;
import com.example.logger.Entities.SystemLog;
import com.example.logger.Persistence.LoggerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoggerService {

    @Autowired
    private LoggerRepository loggerRepository;

    @Transactional
    public void addLog(SystemLog log) {
        loggerRepository.save(log);
    }

    public List<SystemLogDTO> getAllLogs() {
        return loggerRepository.findAll().stream().map(this::convert).collect(Collectors.toList());
    }

    public List<SystemLogDTO> getLastLogs(List<ActivityType> activityType,List<QueryType> queryType,List<Status> status) {
        return loggerRepository.findLatestLogs(PageRequest.of(0, 5),activityType,queryType,status).stream().map(this::convert).collect(Collectors.toList());
    }

    private SystemLogDTO convert(SystemLog log) {
        return new SystemLogDTO(
                log.getTimestamp(),
                log.getFilePath(),
                log.getFileName(),
                log.getActivityType(),
                log.getQueryType(),
                log.getStatus(),
                log.getQuery()
        );
    }

}
