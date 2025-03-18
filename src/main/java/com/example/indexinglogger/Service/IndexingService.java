package com.example.indexinglogger.Service;

import com.example.indexinglogger.Persistence.IndexingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexingService {

    @Autowired
    private IndexingRepository indexingRepository;
}
