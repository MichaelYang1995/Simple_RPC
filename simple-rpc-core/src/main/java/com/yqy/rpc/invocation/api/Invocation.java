package com.yqy.rpc.invocation.api;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.protocol.api.InvokeParam;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @ClassName: Invocation
 * @Description: 远程调用方式的接口,具体有四种实现方式:
 *               1)同步调用
 *               2)异步调用
 *               3)oneWay调用
 *               4)回调调用
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public interface Invocation {
    RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException;
}
