package com.yqy.rpc.protocol.api.support;

import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.GlobalConfig;
import com.yqy.rpc.config.ServiceConfig;
import com.yqy.rpc.protocol.api.Exporter;
import com.yqy.rpc.protocol.api.Protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: AbstractProtocol
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/19
 * @Version: V1.0
 **/
public abstract class AbstractProtocol implements Protocol {
    private Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig){
        this.globalConfig = globalConfig;
    }

    protected GlobalConfig getGlobalConfig(){
        return globalConfig;
    }

    /**
     * 将暴露的服务放入Map中存储
     * @param interfaceClass
     * @param exporter
     */
    public void putExporterMap(Class<?> interfaceClass, Exporter exporter){
        exporterMap.put(interfaceClass.getName(), exporter);
    }

    @Override
    public <T> ServiceConfig<T> referLocalService(String interfaceName) {
        if (!exporterMap.containsKey(interfaceName)){
            //该服务未暴露出来
            throw new RPCException(ExceptionEnum.SERVER_NOT_EXPORTED, "SERVER_NOT_EXPORTED");
        }
        return (ServiceConfig<T>) exporterMap.get(interfaceName).getServiceConfig();
    }




}
