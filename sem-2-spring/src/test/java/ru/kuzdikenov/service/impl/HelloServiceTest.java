package ru.kuzdikenov.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloServiceTest {

    private final HelloService helloService = new HelloService();

    @Test
    void testSayHelloWithName() {
        assertEquals("Hello, Damir", helloService.sayHello("Damir"));
    }

    @Test
    void testSayHelloWithNull() {
        assertEquals("Hello, null", helloService.sayHello(null));
    }
}
