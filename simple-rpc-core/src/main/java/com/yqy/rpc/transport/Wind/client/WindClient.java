package com.yqy.rpc.transport.Wind.client;

import com.yqy.rpc.transport.Wind.codec.WindDecoder;
import com.yqy.rpc.transport.Wind.codec.WindEncoder;
import com.yqy.rpc.transport.Wind.constance.WindConstant;
import com.yqy.rpc.transport.api.support.netty.AbstractNettyClient;
import com.yqy.rpc.transport.constance.CommunicationConstant;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @ClassName: WindClient
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/24
 * @Version: V1.0
 **/
public class WindClient extends AbstractNettyClient {

    @Override
    protected ChannelInitializer initPipeline() {
        return new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline()
                        //空闲检测逻辑处理器
                        .addLast("IdleStateHandler", new IdleStateHandler(0, WindConstant.IDLE_INTERVAL, 0))
                        //Bytebuf -> Message
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(CommunicationConstant.LENGTH_FIELD_LENGTH, CommunicationConstant.LENGTH_ADJUSTMENT))
                        //Message -> Bytebuf
                        .addLast("WindEncoder", new WindEncoder(getGlobalConfig().getSerializer()))
                        //Bytrbuf -> Message
                        .addLast("", new LengthFieldBasedFrameDecoder(CommunicationConstant.MAX_FRAME_LENGTH,
                                CommunicationConstant.LENGTH_FIELD_OFFSET,
                                CommunicationConstant.LENGTH_FIELD_LENGTH,
                                CommunicationConstant.LENGTH_ADJUSTMENT,
                                CommunicationConstant.INITIAL_BYTES_TO_STRIP))
                        //Message -> Message
                        .addLast("", new WindDecoder(getGlobalConfig().getSerializer()))
                        .addLast("", new WindClientHandler(WindClient.this));
            }
        };
    }
}
