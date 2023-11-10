package com.kushui.rpc.client.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rpc-client")
public class RpcServerProperties {
    private String registryAddress;

    private String gatewayId;

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
}
