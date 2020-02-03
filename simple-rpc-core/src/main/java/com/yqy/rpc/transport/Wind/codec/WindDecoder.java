package com.yqy.rpc.transport.Wind.codec;

import com.yqy.rpc.common.domain.Message;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.serialize.api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName: WindDecoder
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/25
 * @Version: V1.0
 **/
public class WindDecoder extends ByteToMessageDecoder {
    private Serializer serializer;

    public WindDecoder(Serializer serializer){
        this.serializer = serializer;
    }

    /**
     * 单例模式,多个pipeline共享handler
     */
    //public static final WindEncoder INSTANCE = new WindEncoder();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();
        if (type == Message.PING){
            out.add(Message.PING_MSG);
        }else if (type == Message.PONG){
            out.add(Message.PONG_MSG);
        }else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            if (type == Message.REQUEST){
                out.add(Message.buildRequest(serializer.deSerializer(bytes, RPCRequest.class)));
            }else if (type == Message.RESPONSE){
                out.add(Message.bulidResponse(serializer.deSerializer(bytes, RPCResponse.class)));
            }
        }
    }
}
