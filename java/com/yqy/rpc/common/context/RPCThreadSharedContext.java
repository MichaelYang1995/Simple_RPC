package com.yqy.rpc.common.context;

import com.yqy.rpc.config.ServiceConfig;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: RPCThreadSharedContext
 * @Description: 存储线程共享的对象,不使用ThreadLocal的原因是:发送请求的线程和接收响应的线程不一定相等
 * @Author: YangQingyuan
 * @Date: 2019/11/29 22:19
 * @Version: 1.0
 */
public class RPCThreadSharedContext {
    private static final Map<String, CompletableFuture<RPCResponse>> RESPONSES = new ConcurrentHashMap<>();

    private static final Map<String, ServiceConfig> HANDLER_MAP = new ConcurrentHashMap<>();

    /* 服务端响应消息到达客户端之后,调用此方法获取请求对应的Future对象 */
    public static CompletableFuture<RPCResponse> getAndRemoveResponseFuture(String requeatID){
        return RESPONSES.remove(requeatID);
    }

    /*将requestID对应的future注册入RESPONSES中,供服务端响应消息处理器使用*/
    public static void registryResponseFuture(String requestID, CompletableFuture<RPCResponse> future){
        RESPONSES.put(requestID, future);
    }

    /* 服务端回调消息到达客户端之后,调用此方法获取对应的ServiceConfig */
    public static ServiceConfig getAndRemoveHandler(String name){
        return HANDLER_MAP.remove(name);
    }

    /* 将被服务端回调的方法注册入HANDLER_MAP中,供服务端回调消息处理器使用 */
    public static void registryCallbackHandler(String name, ServiceConfig serviceConfig){
        HANDLER_MAP.put(name, serviceConfig);
    }
}
