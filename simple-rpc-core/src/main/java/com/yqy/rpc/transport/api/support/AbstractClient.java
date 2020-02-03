package com.yqy.rpc.transport.api.support;

import com.yqy.rpc.config.GlobalConfig;
import com.yqy.rpc.registry.api.ServiceURL;
import com.yqy.rpc.transport.api.Client;

/**
 * @ClassName: AbstractClient
 * @Description: 抽象客户端,主要做一些基础的配置工作供具体的客户端使用
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public abstract class AbstractClient implements Client {
    //为客户端提供服务地址
    private ServiceURL serviceURL;
    //提供用户配置信息
    private GlobalConfig globalConfig;

    public void init(ServiceURL serviceURL, GlobalConfig globalConfig) {
        this.serviceURL = serviceURL;
        this.globalConfig = globalConfig;
        //与远程节点建立连接
        connect();
    }

    //由具体的通信框架实现的方法:与远程服务器节点建立连接
    protected abstract void connect();

    @Override
    public void updateServiceConfig(ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
    }

    @Override
    public ServiceURL getServiceURL() {
        return serviceURL;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
}
