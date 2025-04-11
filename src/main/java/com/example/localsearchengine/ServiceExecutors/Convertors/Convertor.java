package com.example.localsearchengine.ServiceExecutors.Convertors;

public interface Convertor<T,U> {
    U convert(T payload);
}
