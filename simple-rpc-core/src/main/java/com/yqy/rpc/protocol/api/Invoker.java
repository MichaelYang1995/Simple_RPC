package com.yqy.rpc.protocol.api;

import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.registry.api.ServiceURL;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName: Invoker
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/19
 * @Version: V1.0
 **/
public interface Invoker<T> {
    Class<T> getInterfaceClass();

    String getInterfaceName();

    ServiceURL getServiceURL();

    /**
     * 调用服务,返回调用结果
     * @param invokeParam
     * @return
     * @throws RPCException
     */
    RPCResponse invoke(InvokeParam invokeParam) throws RPCException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    boolean isAvailable();
}
