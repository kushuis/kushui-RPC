package com.app.test;

import com.alibaba.fastjson.JSON;

import com.kushui.rpc.client.core.RpcClient;
import com.kushui.rpc.client.generic.GenericService;
import com.kushui.rpc.client.generic.GenericServiceFactory;
import com.kushui.rpc.client.handler.RpcFuture;
import com.kushui.rpc.test.service.HelloService;
import com.kushui.rpc.test.service.Person;
import com.kushui.rpc.test.service.PersonService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class ServiceTest {

    @Autowired
    private RpcClient rpcClient;

    @After
    public void stop() {
        rpcClient.stop();
    }
    //泛化调用测试Person类型，注意GenericServiceInvoker中的版本，注意启动后端提供方
    @Test
    public void helloTest1() {
        Person person = new Person("Yong", "Huang");
        Map<String, Object> dataMap = JSON.parseObject(JSON.toJSONString(person), Map.class);
        GenericService instance = GenericServiceFactory.getInstance("com.app.test.service.HelloService");
        Object result = instance.$invoke("hello", new String[]{"com.app.test.service.Person"}, new Object[]{dataMap});
        System.out.println("===========================");
        System.out.println(result.toString());
        Assert.assertEquals("Hi Yong Huang", result);
    }
    //泛化调用测试Integer类型
    @Test
    public void helloTest3() {

        GenericService instance = GenericServiceFactory.getInstance("com.app.test.service.HelloService");
        Object result = instance.$invoke("hello", new String[]{"java.lang.Integer"}, new Object[]{12});
        System.out.println("===========================");
        System.out.println(result.toString());
        Assert.assertEquals("Hi 12", result);
    }
    //测试provider引入server服务是否能够泛化调用
    //使用这个测试时，需要将RpcAutoConfiguration中RpcClient的bean注释掉，要不然会重复创建bean冲突
    @Test
    public void helloTest4() {
        GenericService instance = GenericServiceFactory.getInstance("cn.bugstack.gateway.rpc.IActivityBooth");
        Object result = instance.$invoke("sayHi", new String[]{"java.lang.String"}, new Object[]{"yasina"});
        System.out.println("===========================");
        System.out.println(result.toString());
        Assert.assertEquals("hi " + "yasina" + " by api-gateway-test-provider", result);
    }

    @Test
    public void test() {
        Person person = new Person("Yong", "Huang");
        System.out.println(Integer.class);
        String s =JSON.toJSONString(person);
        System.out.println(s);
    }
    //普通调用
    @Test
    public void helloTest2() {
        HelloService helloService = rpcClient.createService(HelloService.class, "2.0");
        Person person = new Person("Yong", "Huang");
        String result = helloService.hello(person);
        Assert.assertEquals("Hi Yong Huang", result);
        System.out.println(result);
    }



}
