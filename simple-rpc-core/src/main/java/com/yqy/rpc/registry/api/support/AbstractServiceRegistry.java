package com.yqy.rpc.registry.api.support;

import com.yqy.rpc.config.RegistryConfig;
import com.yqy.rpc.registry.api.ServiceRegistry;

/**
 * @ClassName: AbstractServiceRegistry
 * @Description: 抽象的服务注册中心
 *               只提供registryConfig配置对象
 * @Author: YangQingyuan
 * @Data: 2019/11/16
 * @Version: V1.0
 **/
public abstract class AbstractServiceRegistry implements ServiceRegistry {
    protected RegistryConfig registryConfig;

    public void setRegistryConfig(RegistryConfig registryConfig){
        this.registryConfig = registryConfig;
    }
}
