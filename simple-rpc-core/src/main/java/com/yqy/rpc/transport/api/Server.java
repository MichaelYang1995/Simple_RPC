package com.yqy.rpc.transport.api;

import com.yqy.rpc.common.domain.RPCRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName: Server
 * @Description: 底层通信服务端口
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public interface Server {
    void run();

    void close() throws InterruptedException;

    /**
     * RPC请求处理函数
     * @param request
     * @param ctx ctx用于标记handler,以便任务在线程池中处理完之后能从正确的channel中被刷出
     */
    void handlerRPCRequest(RPCRequest request, ChannelHandlerContext ctx);
}
