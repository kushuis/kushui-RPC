package com.kushui.rpc.test.service;


import com.kushui.rpc.common.annotation.NettyRpcService;

@NettyRpcService(value = HelloService.class, version = "2.0")
public class HelloServiceImpl2 implements HelloService {

    public HelloServiceImpl2() {

    }

    @Override
    public String hello(Integer age) {
        return "Hi " + age;
    }

    @Override
    public String hello(Person person) {
        return "Hi " + person.getFirstName() + " " + person.getLastName();
    }

    @Override
    public String hello(String name, Integer age) {
        return name + " is " + age + " years old";
    }
}
