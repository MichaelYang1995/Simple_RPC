package com.yqy.rpc.cluster.api.support;

import com.yqy.rpc.cluster.api.LoadBalancer;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.config.GlobalConfig;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.protocol.api.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: AbstractLoadBalancer
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/18
 * @Version: V1.0
 **/
@Slf4j
public abstract class AbstractLoadBalancer implements LoadBalancer {
    private GlobalConfig globalConfig;

    private Map<String, ClusterInvoker> interfaceClusters = new ConcurrentHashMap<>();

    @Override
    public <T> Invoker<T> referCluster(ReferenceConfig<T> referenceConfig) {
        String interfaceName = referenceConfig.getInterfaceName();
        if (interfaceClusters.containsKey(interfaceName)){
            return interfaceClusters.get(interfaceName);
        }

        ClusterInvoker clusterInvoker = new ClusterInvoker(referenceConfig.getInterfaceClass(), interfaceName, globalConfig);
        interfaceClusters.put(interfaceName, clusterInvoker);
        return clusterInvoker;
    }

    @Override
    public Invoker select(List<Invoker> availableInvokers, RPCRequest request) {
        if (availableInvokers.size() == 0){
            log.info("当前无可用服务:{}", request.getInterfaceName());
            return null;
        }
        Invoker invoker = doSelect(availableInvokers, request);
//        log.info("LoadBalance:{},choose invoker:{},request:{}",
//                this.getClass().getSimpleName(),
//                invoker.getServiceURL(),
//                request.getRequestID());
        return invoker;
    }

    protected abstract Invoker doSelect(List<Invoker> availableInvokers, RPCRequest request);

    public void updateGlobalConfig(GlobalConfig globalConfig){
        if (this.globalConfig == null){
            this.globalConfig = globalConfig;
        } else {
            if (this.globalConfig.getApplicationConfig() == null){
                this.globalConfig.setApplicationConfig(globalConfig.getApplicationConfig());
            }
            if (this.globalConfig.getRegistryConfig() == null){
                this.globalConfig.setRegistryConfig(globalConfig.getRegistryConfig());
            }
            if (this.globalConfig.getClusterConfig() == null){
                this.globalConfig.setClusterConfig(globalConfig.getClusterConfig());
            }
            if (this.globalConfig.getProtocolConfig() == null){
                this.globalConfig.setProtocolConfig(globalConfig.getProtocolConfig());
            }
        }
    }
}
