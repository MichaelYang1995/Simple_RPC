package com.yqy.rpc.config;

import com.yqy.rpc.cluster.api.LoadBalancer;
import com.yqy.rpc.cluster.api.FaultToleranceHandler;
import com.yqy.rpc.executor.api.TaskExecutor;
import com.yqy.rpc.protocol.api.Protocol;
import com.yqy.rpc.proxy.api.RPCProxyFactory;
import com.yqy.rpc.registry.api.ServiceRegistry;
import com.yqy.rpc.serialize.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: GlobalConfig
 * @Description: 全局配置（单例）
 *               维护几个核心配置类（单例）的实例
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalConfig {
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;

    public Serializer getSerializer(){
        return applicationConfig.getSerializerInstance();
    }

    public RPCProxyFactory getProxyFactory(){
        return applicationConfig.getProxyFactoryInstance();
    }

    public LoadBalancer getLoadBalancer(){
        return clusterConfig.getLoadBalanceInstance();
    }

    public FaultToleranceHandler getFaultToleranceHandler(){
        return clusterConfig.getFaultToleranceHandlerInstance();
    }

    public ServiceRegistry getServiceRegistry(){
        return registryConfig.getRegistryInstance();
    }

    public Protocol getProtocol(){
        return protocolConfig.getProtocolInstance();
    }

    public TaskExecutor getClientExecutor(){
        return protocolConfig.getExecutors().getClient().getTaskExecutorInstance();
    }

    public TaskExecutor getServerExecutor(){
        return protocolConfig.getExecutors().getServer().getTaskExecutorInstance();
    }

    public int getPort(){
        return protocolConfig.getPort();
    }

}
