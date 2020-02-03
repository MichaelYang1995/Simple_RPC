package com.yqy.rpc.invocation.async;

import com.yqy.rpc.common.context.RPCThreadLocalContext;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.invocation.api.support.AbstractInvocation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * @ClassName: AsyncInvocation
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public class AsyncInvocation extends AbstractInvocation {
    @Override
    protected RPCResponse doInvoke(RPCRequest request, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        Future<RPCResponse> future = requestProcessor.apply(request);

        Future<Object> requestFuture = new Future<Object>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                RPCResponse response = future.get();
                if (response.hasError()){
                    throw new ExecutionException(response.getErrorCause());
                }
                return response.getResult();
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                RPCResponse response = future.get(timeout, unit);
                if (response.hasError()){
                    throw new ExecutionException(response.getErrorCause());
                }

                return response.getResult();
            }
        };
        RPCThreadLocalContext.getContext().setFuture(requestFuture);

        //非同步调用,返回null
        return null;
    }
}
