package com.kushui.rpc.test.service;


import com.kushui.rpc.common.annotation.RpcAutowired;

public class FooService implements Foo {
    @RpcAutowired(version = "1.0")
    private HelloService helloService1;

    @RpcAutowired(version = "2.0")
    private HelloService helloService2;

    @Override
    public String say(Integer age) {
        return helloService1.hello(age);
    }
}
