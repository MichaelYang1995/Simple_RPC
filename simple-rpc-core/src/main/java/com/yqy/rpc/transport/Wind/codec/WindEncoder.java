package com.yqy.rpc.transport.Wind.codec;


import com.yqy.rpc.common.domain.Message;
import com.yqy.rpc.serialize.api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: WindEncoder
 * @Description: 面向Wind协议的编码器
 * @Author: YangQingyuan
 * @Data: 2019/11/25
 * @Version: V1.0
 **/
@Slf4j
public class WindEncoder extends MessageToByteEncoder {
    /**
     * 单例模式,多个pipeline共享handler
     */
    //public static final WindEncoder INSTANCE = new WindEncoder();

    private Serializer serializer;

    public WindEncoder(Serializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
       //先写消息类型
        Message message = (Message)msg;
        out.writeByte(message.getType());
        if (message.getType() == Message.REQUEST){
            //序列化Request
            byte[] bytes = serializer.serialize(message.getRequest());
            log.info("Message:{}, 序列化大小:{}", message, bytes.length);
            out.writeBytes(bytes);
            //将message中的Request对象收回
            message.getRequest().recyle();
        }else if (message.getType() == Message.RESPONSE){
            //序列化Response
            byte[] bytes = serializer.serialize(message.getResponse());
            log.info("Message:{}, 序列化大小:{}", message, bytes.length);
            out.writeBytes(bytes);
            //将message中的Response对象收回
            message.getResponse().recyle();
        }
        log.info("完成编码！");
    }
}
