package com.kushui.rpc.client.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;


public class SpringContextHolder {

    private static ApplicationContext applicationContext;

    private SpringContextHolder() {

    }

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 根据类获取bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }


    /**
     * 根据名称获取bean
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

}
