package com.kushui.rpc.server.core;

import com.alibaba.fastjson.JSONObject;
import com.kushui.rpc.common.codec.Beat;
import com.kushui.rpc.common.codec.RpcRequest;
import com.kushui.rpc.common.codec.RpcResponse;
import com.kushui.rpc.common.generic.ReflectUtils;
import com.kushui.rpc.common.generic.RpcResponseUtils;
import com.kushui.rpc.common.generic.SimpleTypeRegistry;
import com.kushui.rpc.common.util.ServiceUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import javafx.beans.binding.ObjectExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    private Map<String, Object> serviceMap;

    private ThreadPoolExecutor threadPoolExecutor;

    public RpcServerHandler(Map<String, Object> serviceMap, ThreadPoolExecutor threadPoolExecutor) {
        this.serviceMap = serviceMap;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        //过滤心跳
        if (Beat.BEAT_ID.equalsIgnoreCase(rpcRequest.getRequestId())) {
            logger.info("接收到心跳信息。。。。");
            return;
        }
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("接收到调用请求：" + rpcRequest.getRequestId());
                String requestId = rpcRequest.getRequestId();
                RpcResponse response = new RpcResponse();
                response.setRequestId(requestId);

                try {
                    Object result = handle(rpcRequest);
                    response.setResult(result);
                } catch (Throwable t) {
                    response.setError(t.toString());
                    logger.error("RPC Server 处理请求异常", t);
                }
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        logger.info("Send response for request " + rpcRequest.getRequestId());
                    }
                });
            }
        });
    }

    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();
        String version = request.getVersion();
        String serviceKey = ServiceUtil.makeServiceKey(className, version);
        Object serviceBean = serviceMap.get(serviceKey);
        String methodName = request.getMethodName();
        if (serviceBean == null) {
            logger.error("Can not find service implement with interface name: {} and version: {}", className, version);
            return null;
        }
        Class<?> serviceClass = serviceBean.getClass();

        Class<?>[] parameterTypes;
        Object[] parameters;
        //判断是否为泛化调用
        if (request.getGeneric()) {
            //为true直接从参数类型由参数类型字符串获得
            parameterTypes = ReflectUtils.convertToParameterTypes(request.getParameterTypeNames());
            //判断参数类型是否为map，基本类型就不是map
            if (SimpleTypeRegistry.isSimpleType(request.getParameterTypeNames()[0])) {
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return RpcResponseUtils.handlerReturnValue(method.invoke(serviceBean, request.getParameters()));
            } else {
                //将map转化为对象
                Object[] temp = request.getParameters();
                String toJSONString = JSONObject.toJSONString(temp[0]);
                Object args = JSONObject.parseObject(toJSONString, parameterTypes[0]);
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                method.setAccessible(true);

                return RpcResponseUtils.handlerReturnValue(method.invoke(serviceBean, args));
            }
        } else {
            //不是泛化调用的情况，直接通过反射获取结果
            parameterTypes = request.getParameterTypes();
            parameters = request.getParameters();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, parameters);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("Server caught exception: " + cause.getMessage());
        ctx.close();
    }
    //判断所有的事件，当发生IdleStateEvent事件时需要关闭channle
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            logger.warn("Channel idle in last {} seconds, close it", Beat.BEAT_TIMEOUT);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
