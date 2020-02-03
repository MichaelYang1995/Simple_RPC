package com.yqy.rpc.protocol.api.support;

import com.yqy.rpc.config.ServiceConfig;
import com.yqy.rpc.protocol.api.Exporter;
import com.yqy.rpc.protocol.api.Invoker;

/**
 * @ClassName: AbstractExporter
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/19
 * @Version: V1.0
 **/
public abstract class AbstractExporter<T> implements Exporter<T> {
    private Invoker<T> invoker;

    private ServiceConfig serviceConfig;

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public ServiceConfig<T> getServiceConfig() {
        return serviceConfig;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setServiceConfig(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }
}
