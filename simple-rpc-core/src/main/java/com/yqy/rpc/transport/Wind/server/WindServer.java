package com.yqy.rpc.transport.Wind.server;

import com.yqy.rpc.transport.Wind.codec.WindDecoder;
import com.yqy.rpc.transport.Wind.codec.WindEncoder;
import com.yqy.rpc.transport.Wind.constance.WindConstant;
import com.yqy.rpc.transport.api.support.netty.AbstractNettyServer;
import com.yqy.rpc.transport.constance.CommunicationConstant;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @ClassName: WindServer
 * @Description: 面向Wind协议的服务端
 *               提供初始化pipeline的方法实现
 * @Author: YangQingyuan
 * @Data: 2019/11/25
 * @Version: V1.0
 **/
public class WindServer extends AbstractNettyServer {
    @Override
    protected ChannelInitializer initPipeline() {
        return new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("IdleStateHandler", new IdleStateHandler(WindConstant.IDLE_INTERVAL*WindConstant.HEART_BEAT_TIMEOUT_MAX_TIMES, 0, 0))
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(CommunicationConstant.LENGTH_FIELD_LENGTH, CommunicationConstant.LENGTH_ADJUSTMENT))
                        .addLast("WindEncoder", new WindEncoder(getGlobalConfig().getSerializer()))
                        .addLast("LengthFieldBasedFrameDecoder",
                                new LengthFieldBasedFrameDecoder(CommunicationConstant.MAX_FRAME_LENGTH,
                                        CommunicationConstant.LENGTH_FIELD_OFFSET,
                                        CommunicationConstant.LENGTH_FIELD_LENGTH,
                                        CommunicationConstant.LENGTH_ADJUSTMENT,
                                        CommunicationConstant.INITIAL_BYTES_TO_STRIP))
                        .addLast("WindDecoder", new WindDecoder(getGlobalConfig().getSerializer()))
                        .addLast("WindServerHandler", new WindServerHandler(WindServer.this));
            }
        };
    }
}
