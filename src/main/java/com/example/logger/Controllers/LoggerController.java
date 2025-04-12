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

    @GetMapping(value = "",params = "activity")
    public ResponseEntity<List<SystemLogDTO>> getLoggerByActivity(@RequestParam ActivityType activity) {return ResponseEntity.ok(loggerService.getLastLogs(activity));}

    @GetMapping(value = "",params = "query")
    public ResponseEntity<List<SystemLogDTO>> getLoggerByActivity(@RequestParam QueryType queryType) {return ResponseEntity.ok(loggerService.getLastLogs(queryType));}

    @GetMapping(value = "",params = "status")
    public ResponseEntity<List<SystemLogDTO>> getLoggerByActivity(@RequestParam Status status) {return ResponseEntity.ok(loggerService.getLastLogs(status));}
}
