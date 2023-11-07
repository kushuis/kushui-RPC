package com.kushui.rpc.client.generic;


import com.kushui.rpc.common.codec.RpcRequest;
import com.kushui.rpc.common.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class GenericServiceInvoker implements ServiceInvoker {

    private final Logger logger = LoggerFactory.getLogger(GenericServiceInvoker.class);

    @Override
    public Object $invoke(String interfaceClassName, String methodName, String[] parameterTypeNames, Object[] args, Boolean generic) {
        RpcRequest request = new RpcRequest();
        String version = "1.0";
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(interfaceClassName);
        request.setMethodName(methodName);
        request.setParameterTypeNames(parameterTypeNames);
        logger.info("===================="+args.toString()+"======================");
        //core代码中已经做了判断参数类型是否为基本类型，如果不是基本类型会封装为map，所以我们在提供方解析的时候要判断其是否为基本类型
        //Object[] arg = SimpleTypeRegistry.isSimpleType(parameterTypeNames[0]) ? params.values().toArray() : new Object[]{params};

        request.setParameters(args);
        request.setVersion(version);
        request.setGeneric(generic);

        String serviceKey = ServiceUtil.makeServiceKey(interfaceClassName, version);
        RpcClientHandler handler = null;
        try {
            handler = ConnectionManager.getInstance().chooseHandler(serviceKey);
        } catch (Exception e) {
            throw new RuntimeException("获取handler出现异常"+ e);
        }
        //发送请求并获得响应结果
        RpcFuture rpcFuture = handler.sendRequest(request);


        return rpcFuture.get();
    }
}
