package com.yqy.rpc.invocation.callback;

import com.yqy.rpc.common.context.RPCThreadSharedContext;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.config.ServiceConfig;
import com.yqy.rpc.invocation.api.support.AbstractInvocation;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @ClassName: CallbacInvocation
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public class CallbacInvocation extends AbstractInvocation {
    @Override
    protected RPCResponse doInvoke(RPCRequest request, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        Object callbackInstance = request.getParameters()[referenceConfig.getCallbackParamIndex()];
        request.getParameters()[referenceConfig.getCallbackParamIndex()] = null;
        registryCallbackHandler(request, referenceConfig, callbackInstance);
        requestProcessor.apply(request);

        //非同步调用,返回null
        return null;
    }

    /**
     * callback注册函数,将callback实例注册到一个线程共享的全局Map中,回调线程根据RPCRequestID取callback实例来执行
     * @param request
     * @param referenceConfig
     * @param callback
     */
    private void registryCallbackHandler(RPCRequest request, ReferenceConfig referenceConfig, Object callback) {
        Class<?> interfaceClass = callback.getClass().getInterfaces()[0];

        ServiceConfig serviceConfig = ServiceConfig.builder()
                .interfaceClass((Class<Object>)interfaceClass)
                .interfaceName(interfaceClass.getName())
                .isCallback(true)
                .ref(callback)
                .build();
        RPCThreadSharedContext.registryCallbackHandler(generateCallbackHandlerKey(request, referenceConfig), serviceConfig);
    }

    private static String generateCallbackHandlerKey(RPCRequest request, ReferenceConfig referenceConfig) {
        return request.getRequestID()+"."+request.getParameterTypes()[referenceConfig.getCallbackParamIndex()];
    }

    private static String generateCallbackHandlerKey(RPCRequest request) {
        return request.getRequestID()+"."+request.getInterfaceName();
    }

}
