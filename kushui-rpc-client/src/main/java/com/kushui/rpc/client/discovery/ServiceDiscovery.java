package com.kushui.rpc.client.discovery;

import com.kushui.rpc.common.zookeeper.CuratorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    private CuratorClient curatorClient;

    public ServiceDiscovery(String registryAddress) {
        this.curatorClient = new CuratorClient(registryAddress);
        discoveryService();
    }
}
