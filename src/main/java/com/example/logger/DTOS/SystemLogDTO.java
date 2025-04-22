package com.example.logger.DTOS;

import com.example.logger.Entities.ActivityType;
import com.example.logger.Entities.QueryType;
import com.example.logger.Entities.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemLogDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Timestamp timestamp;
    private String filePath;
    private String fileName;
    private ActivityType activityType;
    private QueryType queryType;
    private Status status;
    private String message;

}
