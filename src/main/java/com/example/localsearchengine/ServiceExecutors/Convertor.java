package com.example.localsearchengine.ServiceExecutors;

public interface Convertor<T,U> {
    U convert(T payload);
}
