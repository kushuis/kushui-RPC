package com.kushui.rpc.server.core;


import com.kushui.rpc.common.util.ServiceUtil;
import com.kushui.rpc.common.util.ThreadPoolUtil;
import com.kushui.rpc.server.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class NettyServer extends Server {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(NettyServer.class);

    private Thread thread;
    private Map<String,Object> serviceMap = new HashMap<String,Object>();
    private ServiceRegistry serviceRegistry;
    private String serverAddress;

    //当前RPC服务地址和zk地址
    public NettyServer(String serverAddress,String registryAddress){
        this.serviceRegistry = new ServiceRegistry(registryAddress);
        this.serverAddress = serverAddress;
    }

    public void addService(String interfaceName,String version,Object serviceBean){
        logger.info("Adding service, interface: {}, version: {}, bean：{}", interfaceName, version, serviceBean);
        String serviceKey = ServiceUtil.makeServiceKey(interfaceName, version);
        serviceMap.put(serviceKey, serviceBean);
    }

    @Override
    public void start() {
         thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //创建线程池
                ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtil.createThreadPool(NettyServer.class.getSimpleName(), 16, 32);

                NioEventLoopGroup boss = new NioEventLoopGroup();
                NioEventLoopGroup worker = new NioEventLoopGroup();
                ServerBootstrap bootstrap = new ServerBootstrap();
                try {
                    bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                            .childHandler(new RpcServerInitializer(serviceMap, threadPoolExecutor))
                            //等待连接的数量
                            .option(ChannelOption.SO_BACKLOG, 128)
                            //定时检测客户端连接
                            .childOption(ChannelOption.SO_KEEPALIVE, true);
                    //获取host和端口
                    String[] arrays = serverAddress.split(":");
                    String host = arrays[0];
                    int port = Integer.parseInt(arrays[1]);
                    //这里的sync意思是：这个绑定操作是同步的，这意味着调用这个方法不会立即完成，而是在未来的某个时间点完成。为了让主线程等待绑定操作完成
                    ChannelFuture future = bootstrap.bind(host,port).sync();
                    //下面的代码是将服务注册到zookeeper中
                    if (serviceRegistry != null) {
                        serviceRegistry.registerService(host, port, serviceMap);
                    }
                    logger.info("Server started on port {}", port);
                    //注意这个channel是serversocketchannel，和客户端与服务器端建立连接返回的socketchannel不相干
                    //主线程阻塞在这里，当服务关闭时会被唤醒
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    if(e instanceof InterruptedException ){
                        logger.info("Server服务正常关闭");
                    }else {
                        logger.info("Server服务异常关闭");
                    }

                }finally {
                    try {
                        serviceRegistry.unRegistryService();
                        worker.shutdownGracefully();
                        boss.shutdownGracefully();
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }


            }
        });
        thread.start();
    }

    @Override
    public void stop() {
        if(thread != null && thread.isAlive()){
            //唤醒主线程
            thread.interrupt();
            logger.info("stopping............");
        }

    }
}
