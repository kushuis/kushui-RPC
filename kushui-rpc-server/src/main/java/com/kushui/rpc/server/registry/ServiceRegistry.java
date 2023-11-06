package com.kushui.rpc.server.registry;

import com.kushui.rpc.common.config.Constant;
import com.kushui.rpc.common.protocol.RpcProtocol;
import com.kushui.rpc.common.protocol.RpcServiceInfo;
import com.kushui.rpc.common.util.ServiceUtil;
import com.kushui.rpc.common.zookeeper.CuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    //主要用于与zk交互
    private CuratorClient curatorClient;
    //存放path，当服务挂掉以后，要到zk中删除服务
    private List<String> pathList = new ArrayList();

    public ServiceRegistry(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress, 5000);
    }

    public void registerService(String host, int port, Map<String, Object> serviceMap)  {
        //封装RpcProtocol
        ArrayList<RpcServiceInfo> rpcServiceInfos = new ArrayList<>();
        for (String key : serviceMap.keySet()) {
            String[] arrays = key.split(ServiceUtil.SERVICE_CONCAT_TOKEN);
            if (arrays.length == 2) {
                RpcServiceInfo rpcServiceInfo = new RpcServiceInfo();
                rpcServiceInfo.setServiceName(arrays[0]);
                rpcServiceInfo.setVersion(arrays[1]);
                logger.info("Register new service: {} ", key);
                rpcServiceInfos.add(rpcServiceInfo);
            } else {
                logger.warn("service 信息有误，必须为接口名+版本");
                logger.warn(key);
            }
        }

        try {
            RpcProtocol rpcProtocol = new RpcProtocol();
            rpcProtocol.setHost(host);
            rpcProtocol.setPort(port);
            rpcProtocol.setServiceInfoList(rpcServiceInfos);

            String serviceInfo = rpcProtocol.toJson();
            String path = Constant.ZK_DATA_PATH + "-" + rpcProtocol.hashCode();
            byte[] data = serviceInfo.getBytes(StandardCharsets.UTF_8);

            //调用API将服务注册到zookeeper中，bytes为ip地址，端口号，接口名称，接口实例
            path = curatorClient.createPathData(path,data);
            pathList.add(path);
            logger.info("Register {} new service, host: {}, port: {}", rpcServiceInfos.size(), host, port);
        } catch (Exception e) {
            logger.warn("服务注册异常:"+e.getMessage());
        }
        //与zookeeper的交互主要依赖于curatorClient，当curatorClient与zookeeper连接中断
        //zookeeper可能会将curatorClient创建的节点删除，所以我们要监听事件，并且重新注册服务
        curatorClient.addConnectionStateListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState == ConnectionState.RECONNECTED) {
                    logger.info("Connection state: {}, register service after reconnected", connectionState);
                    registerService(host, port, serviceMap);
                }
            }
        });
    }
    public void unRegistryService(){
        logger.info("Unregister all service");

        for (String path : pathList) {
            try {
                this.curatorClient.deletePath(path);
            } catch (Exception ex) {
                logger.error("Delete service path error: " + ex.getMessage());
            }
        }
        this.curatorClient.close();
    }
}
