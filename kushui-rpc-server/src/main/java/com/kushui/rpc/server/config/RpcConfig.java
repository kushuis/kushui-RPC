package com.kushui.rpc.server.config;
import com.kushui.rpc.server.core.RpcServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class RpcConfig {


    @Bean
    public RpcServer rpcServer(RpcClientProperties rpcProperties) {
        return new RpcServer(rpcProperties.getServerAddress(), rpcProperties.getRegistryAddress());
    }
}
