package com.app.test;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kushui.rpc.client.core.RpcClient;
import com.kushui.rpc.common.config.Constant;
import com.kushui.rpc.test.service.Foo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Test
    public List<String> getSystemByGatewayId(){

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("gatewayId", "api-gateway-g4");
        String resultStr;
        try {
            resultStr = HttpUtil.post(Constant.CENTER_ADDRESS + "/wg/admin/config/querySystemByGatewayId", paramMap, 1550);
        } catch (Exception e) {
            throw e;
        }
        System.out.println(resultStr);
        List<String> result = JSON.parseObject(resultStr, new TypeReference<List<String>>() {
        });
        System.out.println(result);
        return result;
    }


}
