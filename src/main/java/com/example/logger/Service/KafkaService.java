package com.example.logger.Service;


import com.example.logger.DTOS.LoggerMessage;
import com.example.logger.Entities.ActivityType;
import com.example.logger.Entities.QueryType;
import com.example.logger.Entities.Status;
import com.example.logger.Entities.SystemLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@Component
public class KafkaService {

    @Autowired
    private FileWriter fileWriter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoggerService loggerService;

    @KafkaListener(topics = "logs", groupId = "group1")
    public void listen(String messageJson) {
        try {
            LoggerMessage logMessage = objectMapper.readValue(messageJson, LoggerMessage.class);

            String log = String.format(
                    """
                    📝 Log Entry
                    ──────────────────────────────
                    📅 Timestamp       : %s
                    👤 User            : %s
                
                    📂 Files Accessed  : %d
                    📃 File Names      : %s
                    🖊️ Description      : %s
                    ──────────────────────────────
                    """,
                    logMessage.getTimestamp(),
                    logMessage.getUser(),
                    logMessage.getActivity().getFileCount(),
                    logMessage.getActivity().getFilesAccessed(),
                    logMessage.getActivity().getDescription()
            );


            fileWriter.write(log + "\n \n \n \n");
            fileWriter.flush();

            SystemLog systemLog = new SystemLog();
            systemLog.setTimestamp(logMessage.getTimestamp());

            for(String file : logMessage.getActivity().getFilesAccessed()) {
                String path = file.substring(0, file.lastIndexOf('/'));
                String fileName = file.substring(file.lastIndexOf('/') + 1);
                String description = logMessage.getActivity().getDescription();
                SystemLog newSystemLog = new SystemLog(
                        null,
                        Timestamp.from(Instant.now()),
                        fileName,
                        path,
                        getActivityType(description),
                        getQueryType(description),
                        getStatus(description)
                );

                loggerService.addLog(systemLog);
            }

        } catch (IOException e) {
            System.out.println("Error deserializing or writing to file: " + e.getMessage());
        }
    }

    private ActivityType getActivityType(String description) {
        if(description.contains("set")) return ActivityType.CREATE;
        if(description.contains("delete") || description.contains("deleted")) return ActivityType.DELETE;
        if(description.contains("update") || description.contains("updated")) return ActivityType.UPDATE;
        return ActivityType.READ;
    }

    private QueryType getQueryType(String description) {
        if(description.contains("contents") || description.contains("content")) return QueryType.CONTENTS;
        if(description.contains("tags") || description.contains("tag")) return QueryType.TAGS;
        if(description.contains("metadata")) return QueryType.METADATA;
        return QueryType.FILE;
    }

    private Status getStatus(String description) {
        if(!description.contains("tried")) return Status.SUCCESS;
        return Status.FAILURE;
    }

}

