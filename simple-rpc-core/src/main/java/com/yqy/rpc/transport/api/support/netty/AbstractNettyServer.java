package com.yqy.rpc.transport.api.support.netty;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.transport.api.support.AbstractServer;
import com.yqy.rpc.transport.api.support.RPCTaskRunner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName: AbstractNettyClient
 * @Description: 基于netty的抽象服务端
 * @Author: YangQingyuan
 * @Data: 2019/11/24
 * @Version: V1.0
 **/
public abstract class AbstractNettyServer extends AbstractServer {
    private ChannelFuture channelFuture;

    private EventLoopGroup boss;
    private EventLoopGroup worker;

    @Override
    protected void doInit(){

    }

    //添加handler,留给具体的Server进行
    protected abstract ChannelInitializer initPipeline();

    @Override
    public void run() {
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initPipeline())
                    //配置属性
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //指定发送缓存区大小
                    .option(ChannelOption.SO_SNDBUF, 32*1024)
                    //指定接收缓存区大小
                    .option(ChannelOption.SO_RCVBUF, 32*1024)
                    .option(ChannelOption.TCP_NODELAY, true);

            //绑定端口,开始监听
            String host = InetAddress.getLocalHost().getHostAddress();
            this.channelFuture = serverBootstrap.bind(host, getGlobalConfig().getPort()).sync();
        }catch (InterruptedException e){
            e.getCause();
        }catch (UnknownHostException e){
            e.getCause();
        }
    }

    /**
     * 关闭服务端连接
     */
    @Override
    public void close() throws InterruptedException {
        //关闭服务端持有的注册中心客户端
        getGlobalConfig().getRegistryConfig().close();
        if(boss != null){
            boss.shutdownGracefully();
        }
        if (worker != null){
            worker.shutdownGracefully();
        }
        if (channelFuture != null){
            channelFuture.channel().close();
        }
    }

    /**
     * RPC请求处理函数
     * @param request
     * @param ctx ctx用于标记handler,以便任务在线程池中处理完之后能从正确的channel中被刷出
     */
    @Override
    public void handlerRPCRequest(RPCRequest request, ChannelHandlerContext ctx) {
        getGlobalConfig().getServerExecutor().submit(new RPCTaskRunner(
                ctx,
                request,
                getGlobalConfig().getProtocol().referLocalService(request.getInterfaceName())
        ));
    }
}
