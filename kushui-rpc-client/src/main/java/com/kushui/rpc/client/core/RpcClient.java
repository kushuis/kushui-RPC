package com.kushui.rpc.client.core;


import com.kushui.rpc.client.discovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RpcClient implements ApplicationContextAware, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ServiceDiscovery serviceDiscovery;

    public RpcClient(String registryAddress) {
        this.serviceDiscovery = new ServiceDiscovery(registryAddress);
    }


    @Override
    public void destroy() throws Exception {

    }

    //下面为普通调用的代码，不注释掉会产生循环依赖的问题
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //        String[] beanNames = applicationContext.getBeanDefinitionNames();
//        for (String beanName : beanNames) {
//            Object bean = applicationContext.getBean(beanName);
//            //获取类的所有字段
//            Field[] fields = bean.getClass().getDeclaredFields();
//            try {
//                for (Field field : fields) {
//                    RpcAutowired rpcAutowired = field.getAnnotation(RpcAutowired.class);
//                    if (rpcAutowired != null) {
//
//                        String version = rpcAutowired.version();
//
//                        //serviceKeys用于初始化建立连接时，只连接引用到的服务
//                        String serviceKey = ServiceUtil.makeServiceKey(field.getType().getName(), version);
////                        serviceKeys.add(serviceKey);
//
//                        field.setAccessible(true);
//                        //首先createService()返回代理对象
//                        //该方法会覆盖bean这个对象的field字段(带有RpcAutowired的方法)为代理对象的处理
//                        field.set(bean, createService(field.getType(), version));
//                    }
//                }
////                serviceDiscovery.initService(serviceKeys);
//            } catch (IllegalAccessException e) {
//                logger.error(e.toString());
//            }
//        }
    }
}
