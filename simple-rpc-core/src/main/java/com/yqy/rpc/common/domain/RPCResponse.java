package com.yqy.rpc.common.domain;

import io.netty.util.Recycler;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: RPCResponse
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/11/30 15:00
 * @Version: 1.0
 */
@Data
public class RPCResponse implements Serializable {
    private String requestID;

    //调用失败原因
    private Throwable errorCause;

    //调用结果
    private Object result;

    private final transient Recycler.Handle<RPCResponse> handle;

    public RPCResponse(Recycler.Handle<RPCResponse> handle){
        this.handle = handle;
    }

    public boolean hasError(){
        return errorCause!=null;
    }

    //回收对象至对象池
    public void recyle(){
        requestID = null;
        errorCause = null;
        result = null;
        handle.recycle(this);
    }

}
