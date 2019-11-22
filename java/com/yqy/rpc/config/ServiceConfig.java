package com.yqy.rpc.config;

import com.yqy.rpc.config.support.AbstractConfig;
import com.yqy.rpc.protocol.api.Exporter;
import com.yqy.rpc.protocol.api.Invoker;
import lombok.Builder;

/**
 * @ClassName: ServiceConfig
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/14
 * @Version: V1.0
 **/
@Builder
public class ServiceConfig<T> extends AbstractConfig {
    private String interfaceName;
    private Class<T> interfaceClass;

    //表示此接口是有一个callback参数用来回调，而不是这个接口为一个回调接口
    private boolean isCallback;

    private String callbackMethod;
    private int callbackParameterIndex = 1;
    private boolean isCallbackInterface;
    private T ref;
    private Exporter<T> exporter;

    /**
     * 暴露服务
     * 这里无法像ReferenceConfig一样搞一个静态cache，都从这里来发现暴露的服务
     * 因为有可能一个invoker在export之后unexport，所以从全局cache取，未必是exported
     * 还是从Protocol里取比较好
     */
    public void expot(){
        Invoker<T> invoker = getApplicationConfig().getProxyFactoryInstance().getInvoker(ref, interfaceClass);
        exporter = getProtocolConfig().getProtocolInstance().export(invoker, this);
    }

}
