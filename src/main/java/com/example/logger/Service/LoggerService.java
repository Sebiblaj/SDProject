package com.example.logger.Service;

import com.example.logger.Persistence.LoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggerService {

    @Autowired
    private LoggerRepository loggerRepository;



}
