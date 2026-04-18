package ru.kuzdikenov.service.impl;

import org.springframework.stereotype.Service;
import ru.kuzdikenov.aop.Benchmarkable;
import ru.kuzdikenov.aop.Metricable;

@Service
public class HelloService {

    @Metricable
    @Benchmarkable
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
