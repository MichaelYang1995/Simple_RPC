package com.yqy.rpc.protocol.wind;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.protocol.api.support.AbstractRemoteInvoker;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @ClassName: WindInvoker
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/21
 * @Version: V1.0
 **/
public class WindInvoker<T> extends AbstractRemoteInvoker<T> {
    public Function<RPCRequest, Future<RPCResponse>> getProcessor(){
        //返回一个与传输层交互的函数
        return request -> getClient().submit(request);
    }
}
