package com.kushui.rpc.client.discovery;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kushui.rpc.client.connect.ConnectionManager;
import com.kushui.rpc.common.config.Constant;
import com.kushui.rpc.common.protocol.RpcProtocol;
import com.kushui.rpc.common.zookeeper.CuratorClient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    private CuratorClient curatorClient;
    private String gatewayId;

    public ServiceDiscovery(String registryAddress, String gatewayId) {
        this.curatorClient = new CuratorClient(registryAddress);
        this.gatewayId = gatewayId;
        discoveryService();
    }

    private void discoveryService() {
        try {
            // Get initial service info
            logger.info("Get initial service info");
            getServiceAndUpdateServer();
            // Add watch listener
            curatorClient.watchPathChildrenNode(Constant.ZK_REGISTRY_PATH, new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                    ChildData childData = pathChildrenCacheEvent.getData();
                    switch (type) {
                        case CONNECTION_RECONNECTED:
                            logger.info("Reconnected to zk, try to get latest service list");
                            getServiceAndUpdateServer();
                            break;
                        case CHILD_ADDED:
                            getServiceAndUpdateServer(childData, PathChildrenCacheEvent.Type.CHILD_ADDED);
                            break;
                        case CHILD_UPDATED:
                            getServiceAndUpdateServer(childData, PathChildrenCacheEvent.Type.CHILD_UPDATED);
                            break;
                        case CHILD_REMOVED:
                            getServiceAndUpdateServer(childData, PathChildrenCacheEvent.Type.CHILD_REMOVED);
                            break;
                    }
                }
            });
        } catch (Exception ex) {
            logger.error("Watch node exception: " + ex.getMessage());
        }
    }

    private void getServiceAndUpdateServer() {
        if (gatewayId == null) {
            try {
                //注意没有配置gatewayId时，在server端也不要配置systemId要不然会出错
                List<String> nodeList = curatorClient.getChildren(Constant.ZK_REGISTRY_PATH);
                List<RpcProtocol> dataList = new ArrayList<>();
                for (String node : nodeList) {
                    logger.debug("Service node: " + node);
                    byte[] bytes = curatorClient.getData(Constant.ZK_REGISTRY_PATH + "/" + node);
                    String json = new String(bytes);
                    RpcProtocol rpcProtocol = RpcProtocol.fromJson(json);
                    dataList.add(rpcProtocol);
                }
                logger.debug("Service node data: {}", dataList);
                //Update the service info based on the latest data
                UpdateConnectedServer(dataList);
            } catch (Exception e) {
                logger.error("从zk中获取node错误: " + e.getMessage());
            }
        } else {
            try {
                //获取systemId集合
                List<String> systems = getSystemByGatewayId(this.gatewayId);
                //获取zk中所有已经启动的server服务集合
                List<String> nodeList = curatorClient.getChildren(Constant.ZK_REGISTRY_PATH);
                List<String> filterList = new ArrayList<>();
                //如果system存在于已经启动了的server中，则放入待连接集合
                for (String system : systems) {
                    if (nodeList.contains(system)) {
                        filterList.add(system);
                    }
                }
                //获取rpcProtocol的data
                List<RpcProtocol> dataList = new ArrayList<>();
                for (String systemId : filterList) {
                    List<String> children = curatorClient.getChildren(Constant.ZK_REGISTRY_PATH + "/" + systemId);
                    logger.debug("Service node: " + children);
                    byte[] bytes = curatorClient.getData(Constant.ZK_REGISTRY_PATH + "/" + systemId + "/" + children.get(0));
                    String json = new String(bytes);
                    RpcProtocol rpcProtocol = RpcProtocol.fromJson(json);
                    dataList.add(rpcProtocol);
                }
                logger.debug("Service node data: {}", dataList);
                //Update the service info based on the latest data
                UpdateConnectedServer(dataList);
            } catch (Exception e) {
                logger.error("Get node exception: " + e.getMessage());
            }
        }
    }

    //问题：建立连接是依靠rpcProtocol的
    public List<String> getSystemByGatewayId(String gatewayId) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("gatewayId", gatewayId);
        String resultStr;
        try {
            resultStr = HttpUtil.post(Constant.CENTER_ADDRESS + "/wg/admin/config/querySystemByGatewayId", paramMap, 1550);
        } catch (Exception e) {
            logger.error("网关id获取系统id错误，可能是没有启动Cetner，或者centerAddress错误,Center地址为：{}", Constant.CENTER_ADDRESS, e);
            throw e;
        }
        List<String> result = JSON.parseObject(resultStr, new TypeReference<List<String>>() {
        });
        return result;
    }

    private void getServiceAndUpdateServer(ChildData childData, PathChildrenCacheEvent.Type type) {
        String path = childData.getPath();
        String data = new String(childData.getData(), StandardCharsets.UTF_8);
        logger.info("Child data updated, path:{},type:{},data:{},", path, type, data);
        RpcProtocol rpcProtocol = RpcProtocol.fromJson(data);
        updateConnectedServer(rpcProtocol, type);
    }

    private void UpdateConnectedServer(List<RpcProtocol> dataList) {
        ConnectionManager.getInstance().updateConnectedServer(dataList);
    }


    private void updateConnectedServer(RpcProtocol rpcProtocol, PathChildrenCacheEvent.Type type) {
        ConnectionManager.getInstance().updateConnectedServer(rpcProtocol, type);
    }

    public void stop() {
        this.curatorClient.close();
    }
}
