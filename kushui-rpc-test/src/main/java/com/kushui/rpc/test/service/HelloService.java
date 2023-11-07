package com.kushui.rpc.test.service;

public interface HelloService {
    String hello(Integer age);

    String hello(Person person);

    String hello(String name, Integer age);
}
