package com.kushui.rpc.client.config;

import com.kushui.rpc.client.core.RpcClient;
import com.kushui.rpc.client.generic.GenericServiceInvoker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcAutoConfiguration {

    RpcAutoConfiguration(ApplicationContext applicationContext) {
        SpringContextHolder.setApplicationContext(applicationContext);
    }

    @Bean
    public GenericServiceInvoker GenericServiceInvoker(){
        return  new GenericServiceInvoker();
    }


    @Bean
    public RpcClient RpcClient(RpcServerProperties rpcServerProperties){
        return  new RpcClient(rpcServerProperties.getRegistryAddress());
    }

}
