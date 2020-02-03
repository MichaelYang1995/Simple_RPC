package com.yqy.rpc.proxy.api.support;

import com.yqy.rpc.common.domain.GlobalRecycler;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.protocol.api.InvokeParam;
import com.yqy.rpc.protocol.api.Invoker;
import com.yqy.rpc.protocol.api.support.AbstractInvoker;
import com.yqy.rpc.protocol.api.support.RPCInvokeParam;
import com.yqy.rpc.proxy.api.RPCProxyFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: AbstractProxyFactory
 * @Description: 抽象的动态代理工厂
 * @Author: YangQingyuan
 * @Data: 2019/11/14
 * @Version: V1.0
 **/
public abstract class AbstractProxyFactory implements RPCProxyFactory {

    private Map<Class<?>, Object> CACHE = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Invoker<T> invoker) {
        if (CACHE.containsKey(invoker.getInterfaceClass())){
            return (T) CACHE.get(invoker.getInterfaceClass());
        }

        T proxy = doCreateProxy(invoker.getInterfaceClass(), invoker);

        CACHE.put(invoker.getInterfaceClass(), proxy);

        return proxy;
    }

    protected abstract  <T> T doCreateProxy(Class<T> InterfaceClass, Invoker<T> invoker);

    protected Object invokeProxyMethod(Invoker invoker, Method method, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] paramTypes = new String[parameterTypes.length];
        for (int i = 0;i < parameterTypes.length;i++){
            paramTypes[i] = parameterTypes[i].getTypeName();
        }
        return invokeProxyMethod(invoker, method.getDeclaringClass().getName(), method.getName(), paramTypes, args);
    }

    protected Object invokeProxyMethod(Invoker invoker, String interfaceName, String methodName, String[] paramTypes, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if ("toString".equals(methodName) && paramTypes.length == 0){
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && paramTypes.length == 0){
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && paramTypes.length == 1){
            return invoker.equals(args[0]);
        }

        RPCRequest request = GlobalRecycler.reuse(RPCRequest.class);
        request.setRequestID(UUID.randomUUID().toString());
        request.setParameters(args);
        request.setParameterTypes(paramTypes);
        request.setInterfaceName(interfaceName);
        request.setMethodName(methodName);
        RPCInvokeParam invokeParam = RPCInvokeParam.builder()
                .rpcRequest(request)
                .referenceConfig(ReferenceConfig.getReferenceConfigByInterfaceName(interfaceName))
                .build();
        RPCResponse response = invoker.invoke(invokeParam);
        Object result = null;
        //若response == null，则属于异步调用：callback、oneway、async
        if (response != null){
            //同步调用
            result =response.getResult();
        }
        response.recyle();
        return result;
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type) {
        return new AbstractInvoker<T>() {
            @Override
            public Class<T> getInterfaceClass() {
                return type;
            }

            @Override
            public String getInterfaceName() {
                return type.getName();
            }

            @Override
            public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
                RPCResponse response = GlobalRecycler.reuse(RPCResponse.class);
                try{
                    response.setRequestID(invokeParam.getRequestID());
                    Method method = proxy.getClass().getMethod(invokeParam.getMethodName(), invokeParam.getParameterTypes());
                    response.setResult(method.invoke(proxy, invokeParam.getParameter()));
                }catch (Exception e){
                    response.setErrorCause(e);
                }
                return response;
            }
        };
    }
}
