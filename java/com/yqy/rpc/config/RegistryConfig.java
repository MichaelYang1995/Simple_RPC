package com.yqy.rpc.config;

import com.yqy.rpc.registry.api.ServiceRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: RegistryConfig
 * @Description: 注册中心配置类，主要配置以下内容：
 *               1）注册中心类型
 *               2）注册中心地址
 *               3）注册中心实例
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryConfig {
    private String type;
    private String address;
    private ServiceRegistry registryInstance;

    public void init(){
        if(registryInstance != null){
            registryInstance.init();
        }
    }

    public void close(){
        if (registryInstance != null){
            registryInstance.close();
        }
    }
}
