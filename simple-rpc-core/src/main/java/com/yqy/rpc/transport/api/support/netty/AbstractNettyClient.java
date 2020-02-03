package com.yqy.rpc.transport.api.support.netty;

import com.yqy.rpc.common.context.RPCThreadSharedContext;
import com.yqy.rpc.common.domain.Message;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.config.ServiceConfig;
import com.yqy.rpc.invocation.callback.CallbackInvocation;
import com.yqy.rpc.transport.Wind.constance.WindConstant;
import com.yqy.rpc.transport.api.support.AbstractClient;
import com.yqy.rpc.transport.api.support.RPCTaskRunner;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: AbstractNettyServer
 * @Description: 基于netty的网络通信客户端,负责创建/关闭客户端连接,并与netty服务连接
 * @Author: YangQingyuan
 * @Data: 2019/11/24
 * @Version: V1.0
 **/
@Slf4j
public abstract class AbstractNettyClient extends AbstractClient {
    //客户端启动对象
    private Bootstrap bootstrap;
    //客户端线程模型
    private NioEventLoopGroup group;
    //客户端连接管道
    private volatile Channel futureChannel;
    //判断客户端是否已被初始化,具备内存可见性,防止重复初始化
    private volatile boolean initialized;
    //判断客户端是否已经关闭,具备内存可见性,防止重复关闭
    private volatile boolean destoryed;
    //判断客户端是否是由于服务端下线导致的关闭,如果是,则不再重连
    private volatile boolean closedByServer;
    //重连执行器
    private RetryExecutor retryExecutor = new RetryExecutor();

    //初始化ChannelPipeline,添加Channelhandler由具体的client完成
    protected abstract ChannelInitializer initPipeline();

    private class RetryExecutor implements Runnable {

        @Override
        public void run() {
            if (!closedByServer){
                try{
                    if (futureChannel != null && futureChannel.isOpen()){
                        futureChannel.close().sync();
                    }
                    doConnect();
                } catch (InterruptedException e) {
                    //出现异常,反复重连
                    log.info("重连失败,{}秒后重连", WindConstant.IDLE_INTERVAL);
                    futureChannel.eventLoop().schedule(retryExecutor, WindConstant.IDLE_INTERVAL, TimeUnit.SECONDS);
                }
            }else {
                log.info("由于服务端下线,无法重连...");
            }
        }
    }

    protected void doConnect() throws InterruptedException {
        String address = getServiceURL().getServiceAddress();
        String host = address.split(":")[0];
        Integer port = Integer.parseInt(address.split(":")[1]);
        //同步连接服务端
        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.futureChannel = future.channel();
        log.info("客户端已经连接到:{}", address);
        log.info("初始化客户端完毕");
        initialized = true;
        destoryed = false;
    }

    @Override
    protected synchronized void connect(){
        if(initialized){
            return;
        }
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(initPipeline())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        try {
            doConnect();
            log.info("netty客户端启动:{}", this.getClass().getSimpleName());
        }catch (Exception e){
            log.info("连接服务器失败...");
            e.printStackTrace();
            handlerException(e);
        }
    }

    /**
     * 异常逻辑处理器
     * @param throwable
     */
    @Override
    public void handlerException(Throwable throwable){
        log.error("", throwable);
        log.info("开始尝试重连...");
        try{
            reconnect();
        }catch (Exception e){
            log.info("多次重连失败...");
            close();
            throw new RPCException(ExceptionEnum.FAIL_TO_CONNECT_TO_SERVER, "FAIL_TO_CONNECT_TO_SERVER");
        }
    }

    protected void reconnect(){
        if (destoryed){
            retryExecutor.run();
        }
    }

    @Override
    public void close(){
        try {
            if (futureChannel != null && futureChannel.isOpen()){
                futureChannel.close().sync();
            }
            destoryed = true;
            closedByServer = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (group != null && !group.isShutdown() && !group.isTerminated()){
                //优雅停机
                group.shutdownGracefully();
            }
        }
    }

    @Override
    public Future<RPCResponse> submit(RPCRequest request){
        if (!initialized){
            connect();
        }

        if (destoryed || closedByServer){
            throw new RPCException(ExceptionEnum.SUBMIT_AFTER_ENDPOINT_CLOSE, "FAIL_TO_CONNECT_TO_SERVER");
        }

        log.info("客户端发起调用请求");
        CompletableFuture<RPCResponse> responseFuture = new CompletableFuture<>();
        RPCThreadSharedContext.registryResponseFuture(request.getRequestID(), responseFuture);
        //将请求写入Channel,并出站
        this.futureChannel.writeAndFlush(Message.buildRequest(request));

        log.info("请求已经发送至: {}", getServiceURL().getServiceAddress());
        return responseFuture;
    }

    /**
     * 调用结果处理器
     * @param response
     */
    @Override
    public void handlerRPCResponse(RPCResponse response){
        //根据响应对象中的RequestID,获取对应的future对象
        CompletableFuture<RPCResponse> future = RPCThreadSharedContext.getAndRemoveResponseFuture(response.getRequestID());
        //将该future设置为已完成
        //1)同步调用:直接调用get()方法获取response对象
        //2)异步调用:当client线程需要response时,调用get()方法,获取response对象
        future.complete(response);
    }

    /**
     * 回调请求逻辑处理器
     * 此时Client客户端作为服务端的角色,接收并响应Server服务端的回调请求,调用本地方法返回结果给Server服务端
     * @param request
     * @param ctx
     */
    @Override
    public void handlerCallbackRequest(RPCRequest request, ChannelHandlerContext ctx){
        ServiceConfig serviceConfig = RPCThreadSharedContext.getAndRemoveHandler(
                CallbackInvocation.generateCallbackHandlerKey(request, ReferenceConfig.getReferenceConfigByInterfaceName(request.getInterfaceName()))
        );

        //将回调任务提交给线程池处理
        getGlobalConfig().getClientExecutor().submit(new RPCTaskRunner(ctx, request, serviceConfig));
    }

    @Override
    public boolean isAvailable(){
        return initialized && !destoryed;
    }
}
