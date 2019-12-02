package com.yqy.rpc.common.enumeration;

import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.invocation.api.Invocation;
import com.yqy.rpc.invocation.async.AsyncInvocation;
import com.yqy.rpc.invocation.callback.CallbackInvocation;
import com.yqy.rpc.invocation.oneway.OneWayInvocation;
import com.yqy.rpc.invocation.sync.SyncInvocaton;
import com.yqy.rpc.protocol.api.InvokeParam;
import com.yqy.rpc.protocol.api.support.RPCInvokeParam;

/**
 * @ClassName: InvocationType
 * @Description: 调用方式枚举类
 *               根据@RPCReference注解属性来选择枚举类
 * @Author: YangQingyuan
 * @Date: 2019/12/1 20:41
 * @Version: 1.0
 */
public enum InvocationType {
    SYNC(new SyncInvocaton()),            //同步调用
    ASYNC(new AsyncInvocation()),         //异步调用
    CALLBACK(new CallbackInvocation()),   //回调方式
    ONEWAY(new OneWayInvocation());       //oneWay调用

    private Invocation invocation;

    InvocationType(Invocation invocation){
        this.invocation = invocation;
    }

    public static Invocation get(InvokeParam invokeParam){
        ReferenceConfig referenceConfig = ((RPCInvokeParam)invokeParam).getReferenceConfig();
        if (referenceConfig.isAsync()){
            return ASYNC.invocation;
        }else if (referenceConfig.isCallback()){
            return CALLBACK.invocation;
        }else if (referenceConfig.isOneWay()){
            return ONEWAY.invocation;
        }else{
            return SYNC.invocation;
        }
    }
}
