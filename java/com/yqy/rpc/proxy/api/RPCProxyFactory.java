package com.yqy.rpc.proxy.api;

/**
 * @InterfaceName: RPCProxyFactory
 * @Description: RPC代理工厂接口类
 *               1）当应用为客户端时，代理工厂负责生产远程服务的本地代理
 *               2）当应用为服务端时，代理工厂负责生产本地服务的本地代理
 * @Author: YangQingyuan
 * @Data: 2019/11/14
 * @Version: V1.0
 **/
public interface RPCProxyFactory {

    <T> T createProxy(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type);
}
