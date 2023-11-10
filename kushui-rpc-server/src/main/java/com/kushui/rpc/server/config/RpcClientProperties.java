package com.kushui.rpc.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rpc-server")
public class RpcClientProperties {

    private String serverAddress;

    private String registryAddress;

    private String systemId;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
}
