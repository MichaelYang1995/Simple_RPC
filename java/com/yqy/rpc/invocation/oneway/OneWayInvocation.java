package com.yqy.rpc.invocation.oneway;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.invocation.api.support.AbstractInvocation;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @ClassName: CallbacInvocation
 * @Description: oneWay方式调用,即调用方不关心调用结果,提交完请求就直接返回null
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public class OneWayInvocation extends AbstractInvocation {
    @Override
    protected RPCResponse doInvoke(RPCRequest request, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        requestProcessor.apply(request);

        //非同步调用,返回null
        return null;
    }
}
