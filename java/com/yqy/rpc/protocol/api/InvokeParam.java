package com.yqy.rpc.protocol.api;

/**
 * @ClassName: InvokeParam
 * @Description: 服务参数接口:定义了调用一个服务需要哪些参数
 * @Author: YangQingyuan
 * @Data: 2019/11/19
 * @Version: V1.0
 **/
public interface InvokeParam {
    String getInterfaceName();

    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getParameter();

    String getRequestID();
}
