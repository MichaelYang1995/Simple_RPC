package com.yqy.rpc.transport.api;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.registry.api.ServiceURL;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Future;

/**
 * @ClassName: Client
 * @Description: 底层通信客户端接口
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public interface Client {
    Future<RPCResponse> submit(RPCRequest request);

    void close();

    /**
     * 异常逻辑处理器
     * @param throwable
     */
    void handlerException(Throwable throwable);

    /**
     * 调用结果处理器
     * @param response
     */
    void handlerRPCResponse(RPCResponse response);

    /**
     * 回调请求逻辑处理器
     * @param request
     * @param ctx
     */
    void handlerCallbackRequest(RPCRequest request, ChannelHandlerContext ctx);

    /**
     * 更新服务配置
     * @param serviceURL
     */
    void updateServiceConfig(ServiceURL serviceURL);

    /**
     * 获取与该客户端实例相连接的服务信息
     * @return
     */
    ServiceURL getServiceURL();

    boolean isAvailable();
}
