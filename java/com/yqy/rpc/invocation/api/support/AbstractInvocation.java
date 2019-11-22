package com.yqy.rpc.invocation.api.support;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.invocation.api.Invocation;
import com.yqy.rpc.protocol.api.InvokeParam;
import com.yqy.rpc.protocol.api.support.RPCInvokeParam;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @ClassName: AbstractInvocation
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public abstract class AbstractInvocation implements Invocation {
    @Override
    public RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException {
        RPCResponse response;
        RPCRequest request = ((RPCInvokeParam)invokeParam).getRpcRequest();
        ReferenceConfig referenceConfig = ((RPCInvokeParam)invokeParam).getReferenceConfig();
        try {
            response = doInvoke(request, referenceConfig, requestProcessor);
        }catch (Throwable t){
            t.printStackTrace();
            throw new RPCException(ExceptionEnum.TRANSPORT_FAILURE, "TRANSPORT_FAILURE");
        }

        return response;
    }

    //具体的调用方法
    protected abstract RPCResponse doInvoke(RPCRequest request, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable;
}
