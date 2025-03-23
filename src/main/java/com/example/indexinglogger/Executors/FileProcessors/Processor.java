package com.example.indexinglogger.Executors.FileProcessors;

import java.util.List;

public interface Processor <T,U>{
    List<T> process(U u);
    List<T> process(List<U> u);

}
