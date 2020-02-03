package com.yqy.rpc.protocol.api;

import com.yqy.rpc.config.ServiceConfig;

/**
 * @ClassName: Exporter
 * @Description: 服务暴露之后的抽象调用接口:实质上就是将Invoker和ServiceConfig进行了封装
 * @Author: YangQingyuan
 * @Data: 2019/11/19
 * @Version: V1.0
 **/
public interface Exporter<T> {
    Invoker<T> getInvoker();

    ServiceConfig<T> getServiceConfig();
}
