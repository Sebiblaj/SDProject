package com.example.logger.Controllers;

import com.example.logger.DTOS.SystemLogDTO;
import com.example.logger.Entities.ActivityType;
import com.example.logger.Entities.QueryType;
import com.example.logger.Entities.Status;
import com.example.logger.Service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("logger")
public class LoggerController {

    @Autowired
    private LoggerService loggerService;

    @GetMapping(value = "")
    public ResponseEntity<List<SystemLogDTO>> getLogger() { return ResponseEntity.ok(loggerService.getAllLogs());}

    @GetMapping(value = "params")
    public ResponseEntity<List<SystemLogDTO>> getLoggerByActivity(@RequestParam(required = false) List<ActivityType> activity,
                                                                  @RequestParam(required = false) List<QueryType> queryType,
                                                                  @RequestParam(required = false) List<Status> status) {return ResponseEntity.ok(loggerService.getLastLogs(activity,queryType,status));}

}
