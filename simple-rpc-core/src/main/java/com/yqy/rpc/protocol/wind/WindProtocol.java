package com.yqy.rpc.protocol.wind;

import com.yqy.rpc.client.filter.Filter;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.config.ServiceConfig;
import com.yqy.rpc.protocol.api.Exporter;
import com.yqy.rpc.protocol.api.Invoker;
import com.yqy.rpc.protocol.api.support.AbstractRemoteProtocol;
import com.yqy.rpc.registry.api.ServiceURL;
import com.yqy.rpc.transport.Wind.client.WindClient;
import com.yqy.rpc.transport.Wind.server.WindServer;
import com.yqy.rpc.transport.api.Client;
import com.yqy.rpc.transport.api.Server;

import java.util.List;

/**
 * @ClassName: WindProtocol
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/21
 * @Version: V1.0
 **/
public class WindProtocol extends AbstractRemoteProtocol {
    /**
     * 抽象方法:目标服务器未与本机相连的时候,由具体的protocol实例来调用该方法创建连接，并返回client客户端
     */
    @Override
    protected Client doInitClient(ServiceURL serviceURL){
        WindClient windClient = new WindClient();
        windClient.init(serviceURL, getGlobalConfig());
        return windClient;
    }

    @Override
    protected Server doOpenServer() {
        WindServer windServer = new WindServer();
        windServer.init(getGlobalConfig());
        windServer.run();
        return windServer;
    }

    @Override
    public <T> Invoker<T> refer(ServiceURL serviceURL, ReferenceConfig referenceConfig) {
        //创建了一个面向Wind协议的调用者,负责提供getProcessor()方法与transport层交互
        WindInvoker<T> windInvoker = new WindInvoker<>();
        windInvoker.setInterfaceName(referenceConfig.getInterfaceName());
        windInvoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        windInvoker.setGlobalConfig(getGlobalConfig());
        windInvoker.setClient(initClient(serviceURL));
        List<Filter> filters = referenceConfig.getFilters();
        if (filters.size()==0){
            return windInvoker;
        }

        return windInvoker.buildFilterChain(filters);
    }

    @Override
    public <T> Exporter<T> export(Invoker localInvoker, ServiceConfig serviceConfig) {
        WindExporter<T> windExporter = new WindExporter<>();
        windExporter.setInvoker(localInvoker);
        windExporter.setServiceConfig(serviceConfig);

        putExporterMap(localInvoker.getInterfaceClass(), windExporter);
        //暴露服务之前先打开服务连接，防止暴露服务之后,还没打开服务连接,
        //就已经有客户端从注册中心获取了该服务地址并开始尝试建立连接
        openServer();
        try {
            serviceConfig.getRegistryConfig().getRegistryInstance().register(
                    //主机IP+端口号
                    "192.168.1.116"+":"+getGlobalConfig().getPort(),
                    localInvoker.getInterfaceName(),
                    localInvoker.getInterfaceClass());
        }catch (Exception e){
            throw new RPCException(ExceptionEnum.FAIL_TO_GET_LOCALHOST_ADDRESS, "FAIL_TO_GET_LOCALHOST_ADDRESS");
        }
        return windExporter;
    }
}
