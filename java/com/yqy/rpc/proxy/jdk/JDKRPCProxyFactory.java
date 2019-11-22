package com.yqy.rpc.proxy.jdk;

import com.yqy.rpc.proxy.api.support.AbstractProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName: JDKProxyFactory
 * @Description: JDK动态代理实现的代理工厂
 * @Author: YangQingyuan
 * @Data: 2019/11/14
 * @Version: V1.0
 **/
public abstract class JDKRPCProxyFactory extends AbstractProxyFactory {
    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return JDKRPCProxyFactory.this.invokeProxyMethod(invoker, method, args);
                    }
                });
    }
}
