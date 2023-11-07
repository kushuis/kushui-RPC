package com.app.test;


import com.kushui.rpc.client.core.RpcClient;
import com.kushui.rpc.test.service.Foo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class ServiceTest2 {
    @Autowired
    private Foo foo;

    @Autowired
    private RpcClient rpcClient;

    @After
    public void stop() {
        rpcClient.stop();
    }

    @Test
    public void say() {
        String result = foo.say(1);
        Assert.assertEquals("Hello Foo", result);
        System.out.println("==========="+result+"===========");
    }


}
