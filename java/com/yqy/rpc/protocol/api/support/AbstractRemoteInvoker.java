package com.yqy.rpc.protocol.api.support;

import com.yqy.rpc.registry.api.ServiceURL;

/**
 * @ClassName: AbstractRemoteInvoker
 * @Description: 抽象的远程服务调用者
 * @Author: YangQingyuan
 * @Data: 2019/11/20
 * @Version: V1.0
 **/
public class AbstractRemoteInvoker<T> extends AbstractInvoker<T>{

    //由于是远程调用,因此需要一个客户端实例
    private Client client;

    @Override
    public ServiceURL getServiceURL(){
        return getClient().getServiceURL();
    }

    protected Client getClient(){
        return client;
    }

    @Override
    public boolean isAvailable(){
        return getClient().isAvailable();
    }

    public void setClient(Client client){
        this.client = client;
    }
}
