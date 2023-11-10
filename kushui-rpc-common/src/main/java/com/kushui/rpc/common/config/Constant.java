package com.kushui.rpc.common.config;


public interface Constant {
    int ZK_SESSION_TIMEOUT = 5000;
    int ZK_CONNECTION_TIMEOUT = 5000;

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
    String CENTER_ADDRESS = "localhost:8001";
    String ZK_NAMESPACE = "netty-rpc";
}
