package com.yqy.rpc.config.support;

import com.yqy.rpc.config.*;

/**
 * @ClassName: AbstractConfig
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
public class AbstractConfig {
    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig){
        this.globalConfig = globalConfig;
    }

    public ApplicationConfig getApplicationConfig(){
        return globalConfig.getApplicationConfig();
    }

    public ClusterConfig getClusterConfig(){
        return globalConfig.getClusterConfig();
    }

    public ProtocolConfig getProtocolConfig(){
        return globalConfig.getProtocolConfig();
    }

    public RegistryConfig getRegistryConfig(){
        return globalConfig.getRegistryConfig();
    }
}
