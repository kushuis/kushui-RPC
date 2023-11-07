package com.kushui.rpc.client.generic;




import com.kushui.rpc.client.config.SpringContextHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class GenericServiceFactory {

    /**
     * 实例缓存，key:接口类名
     */
    private static final Map<String, GenericService> INSTANCE_MAP = new ConcurrentHashMap<>();

    private GenericServiceFactory() {}

    /**
     * @param interfaceClassName
     * @return
     */
    public static GenericService getInstance(String interfaceClassName) {
        return INSTANCE_MAP.computeIfAbsent(interfaceClassName, clz -> {
            GenericServiceInvoker serviceInvoker = SpringContextHolder.getBean(GenericServiceInvoker.class);
//            GenericServiceInvoker serviceInvoker = new GenericServiceInvoker();
            DefaultGenericService genericService = new DefaultGenericService(serviceInvoker, interfaceClassName);
            return genericService;
        });
    }
}
