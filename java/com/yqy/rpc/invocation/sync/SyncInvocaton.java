package com.yqy.rpc.invocation.sync;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.pq.rpc.config.ReferenceConfig;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.invocation.api.support.AbstractInvocation;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @ClassName: CallbacInvocation
 * @Description: 同步调用
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
@Slf4j
public class SyncInvocaton extends AbstractInvocation {
    @Override
    protected RPCResponse doInvoke(RPCRequest request, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        Future<RPCResponse> future = requestProcessor.apply(request);   //直接提交请求
        //get()方法使得线程阻塞在此
        RPCResponse response = future.get(referenceConfig.getTimeout(),TimeUnit.MILLISECONDS);
        log.info("收到RPC调用结果:"+response);
        return response;
    }
}
