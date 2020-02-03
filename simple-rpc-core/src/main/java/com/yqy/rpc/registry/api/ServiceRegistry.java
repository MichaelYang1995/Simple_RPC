package com.yqy.rpc.registry.api;

/**
 * @ClassName: ServiceOfflineCallback
 * @Description: 服务注册中心接口
 *               定义了初始化注册中心、服务发现、服务注册、关闭注册中心等方法
 * @Author: YangQingyuan
 * @Data: 2019/11/16
 * @Version: V1.0
 **/
public interface ServiceRegistry {

    void init();

    /**
     * 服务发现，并更新注册中心服务列表，用于consumer方
     * @param interfaceName
     * @param serviceAddOrUpdateCallback
     * @param serviceOfflineCallback
     */
    void discover(String interfaceName, ServiceAddOrUpdateCallback serviceAddOrUpdateCallback, ServiceOfflineCallback serviceOfflineCallback);

    /**
     * 服务注册，用于provider方
     * @param serviceAddress
     * @param interfaceName
     * @param interfaceClass
     */
    void register(String serviceAddress, String interfaceName, Class<?> interfaceClass);

    void close() throws InterruptedException;
}
