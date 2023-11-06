package com.kushui.rpc.common.generic;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public class RpcResponseUtils {

    private static List<Class> JDK_CLASS = Lists.newArrayList(
            Long.class,
            Integer.class,
            Byte.class,
            Short.class,
            Double.class,
            BigDecimal.class,
            String.class
    );

    /**
     * 泛化调用结果处理
     *
     * @param returnValue
     * @return
     */
    public static Object handlerReturnValue(Object returnValue)  {
        if (returnValue == null) {
            return null;
        }
        Class<?> returnValueClass = returnValue.getClass();
        if (returnValueClass.isPrimitive()) {
            return returnValue;
        }
        if (JDK_CLASS.contains(returnValueClass)) {
            return returnValue;
        }
        // POJO  转map
        Map<String, Object> dataMap = JSON.parseObject(JSON.toJSONString(returnValue), Map.class);
        return dataMap;
    }


}
