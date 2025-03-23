package com.example.indexinglogger.Executors.HTTPProcessors;

import java.util.List;

public interface HttpProcessor <U>{

    default List<U> processList(Object ... args){
        return List.of();
    }

    default U process(Object ... args){
        return null;
    }
}
