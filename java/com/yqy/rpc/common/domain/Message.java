package com.yqy.rpc.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: Message
 * @Description: 消息对象,封装了RPC请求对象、RPC响应对象以及消息类型
 * @Author: YangQingyuan
 * @Date: 2019/11/30 13:44
 * @Version: 1.0
 */
@Data
public class Message implements Serializable {
    private RPCRequest request;

    private RPCResponse response;

    private byte type;

    public Message(byte type){
        this.type = type;
    }

    public Message buildRequest(RPCRequest request){
        return new Message(request, null, REQUEST);
    }

    public Message bulidResponse(RPCResponse response){
        return new Message(null, response, RESPONSE);
    }

    //所有消息类型
    public static final byte PING = 1;        //心跳检测PING类型
    public static final byte PONG = 1<<1;     //心跳检测PONG类型
    public static final byte REQUEST = 1<<2;  //请求消息类型
    public static final byte RESPONSE = 1<<3; //响应消息类型
    public static final Message PING_MSG = new Message(PING);  //PING消息对象
    public static final Message PONG_MSG = new Message(PONG);  //PONG消息对象
}
