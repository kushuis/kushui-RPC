package com.kushui.rpc.server.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {

    public RpcServerInitializer(Map<String, Object> serviceMap, ThreadPoolExecutor threadPoolExecutor) {

    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

    }
}
