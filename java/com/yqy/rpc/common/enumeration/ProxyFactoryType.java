package com.yqy.rpc.common.enumeration;

import com.yqy.rpc.common.enumeration.support.ExtensionBaseType;
import com.yqy.rpc.proxy.api.RPCProxyFactory;
import com.yqy.rpc.proxy.jdk.JDKRPCProxyFactory;

/**
 * @ClassName: ProxyFactoryType
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/1 21:01
 * @Version: 1.0
 */
public enum ProxyFactoryType implements ExtensionBaseType<RPCProxyFactory> {
    JDK(new JDKRPCProxyFactory());  //jdk实现的动态代理工厂

    private RPCProxyFactory rpcProxyFactory;

    ProxyFactoryType(RPCProxyFactory rpcProxyFactory){
        this.rpcProxyFactory = rpcProxyFactory;
    }

    @Override
    public RPCProxyFactory getInstance() {
        return rpcProxyFactory;
    }
}
