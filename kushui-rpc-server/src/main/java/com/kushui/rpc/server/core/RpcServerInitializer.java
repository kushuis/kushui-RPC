package com.kushui.rpc.server.core;

import com.kushui.rpc.common.codec.*;
import com.kushui.rpc.common.serializer.Serializer;
import com.kushui.rpc.common.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {

    private Map<String, Object> serviceMap;
    private ThreadPoolExecutor threadPoolExecutor;
    public RpcServerInitializer(Map<String, Object> serviceMap, ThreadPoolExecutor threadPoolExecutor) {
        this.serviceMap = serviceMap;
        this.threadPoolExecutor = threadPoolExecutor;

    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        Serializer serializer = KryoSerializer.class.newInstance();
        ChannelPipeline cp = socketChannel.pipeline();
        //这个心跳事件主要用于关闭channel，90s内没有读写事件时触发
        cp.addLast(new IdleStateHandler(0, 0, Beat.BEAT_TIMEOUT, TimeUnit.SECONDS));
        //最大长度，第几个字节开始是长度，长度占用几个字节，长度字节后第几个开始是正文
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder(RpcRequest.class, serializer));
        cp.addLast(new RpcEncoder(RpcResponse.class, serializer));
        cp.addLast(new RpcServerHandler(serviceMap, threadPoolExecutor));
    }
}
