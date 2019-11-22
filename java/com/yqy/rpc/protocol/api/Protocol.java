package com.yqy.rpc.protocol.api;

import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.config.ServiceConfig;
import com.yqy.rpc.registry.api.ServiceURL;

/**
 * @ClassName: Protocol
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/19
 * @Version: V1.0
 **/
public interface Protocol {
    <T> Invoker<T> refer(ServiceURL serviceURL, ReferenceConfig referenceConfig);

    <T> Exporter<T> export(Invoker localInvoker, ServiceConfig serviceConfig);

    <T> ServiceConfig<T> referLocalService(String interfaceName);

    void close();
}
