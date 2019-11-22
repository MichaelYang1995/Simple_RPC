package com.yqy.rpc.cluster.api.support;

import com.yqy.rpc.common.context.RPCThreadLocalContext;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.GlobalConfig;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.protocol.api.InvokeParam;
import com.yqy.rpc.protocol.api.Invoker;
import com.yqy.rpc.protocol.api.support.AbstractRemoteProtocol;
import com.yqy.rpc.protocol.api.support.RPCInvokeParam;
import com.yqy.rpc.registry.api.ServiceURL;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName: ClusterInvoker
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/18
 * @Version: V1.0
 **/
@Slf4j
public class ClusterInvoker<T> implements Invoker<T> {
    private String interfaceName;

    private Class<T> interfaceClass;

    private GlobalConfig globalConfig;

    /**
     * 服务调用方维护的服务提供者列表
     * k: 服务器地址address ;
     * v: protocol层的Invoker
     */
    private Map<String, Invoker<T>> protocolInvokers = new ConcurrentHashMap<>();

    ClusterInvoker(Class<T> interfaceClass, String interfaceName, GlobalConfig globalConfig){
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceName;
        this.globalConfig = globalConfig;
        init();
    }

    private void init(){
        globalConfig.getRegistryConfig()
                .getRegistryInstance()
                .discover(interfaceName, this::addOrUpdate, this::removeNotExisted);
    }

    private synchronized void addOrUpdate(ServiceURL serviceURL){
        String address = serviceURL.getServiceAddress();
        if (protocolInvokers.containsKey(address)){
            //服务地址没有改变,只是配置被改变
            //只需要在protocol层对配置进行更新
            if (globalConfig.getProtocol() instanceof AbstractRemoteProtocol){
                AbstractRemoteProtocol protocol = (AbstractRemoteProtocol) globalConfig.getProtocol();
                //更新底层配置信息
                protocol.updateEndpointConfig(serviceURL);
            }
        }else {
            //新增服务地址
            log.info("NEW SERVER ADDED,INTERFACE_NAME:"+interfaceName+",NEW_SERVICE_URL:"+serviceURL);
            //引用远程服务,获得protocol层的Invoker对象
            Invoker protocolInvoker = globalConfig.getProtocolConfig()
                    .getProtocolInstance()
                    .refer(serviceURL, ReferenceConfig.getReferenceConfigByInterfaceName(interfaceName));
            protocolInvokers.put(address, protocolInvoker);
        }
    }

    private synchronized void removeNotExisted(List<ServiceURL> serviceURLS){
        //将serviceURLS列表转化为address-->serviceURL的Map
        Map<String, ServiceURL> newAddressServiceURLS = serviceURLS.stream().collect(Collectors.toMap(
                ServiceURL::getServiceAddress,
                url->url));
        Iterator<Map.Entry<String, Invoker<T>>> iter = protocolInvokers.entrySet().iterator();
        //遍历当前可用服务列表,若没有出现在新列表中,则删除此服务,并断开与服务终端的连接
        while (iter.hasNext()){
            Map.Entry<String, Invoker<T>> cur = iter.next();
            if (!newAddressServiceURLS.containsKey(cur.getKey())){
                log.info("PROVIDER:"+cur.getKey()+"IS ALREDAY OFFLINE");
                ((AbstractRemoteProtocol)globalConfig.getProtocol()).closeEndpoint(cur.getKey());

                iter.remove();
            }
        }
    }

    @Override
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public ServiceURL getServiceURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RPCResponse invoke(InvokeParam invokeParam) throws RPCException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Invoker invoker = doselect(getAvailableInvokers(), invokeParam);
        //将该invoker设置为当前线程的Invoker对象,这样就保证了每个线程对应一个独立的invoker
        RPCThreadLocalContext.getContext().setInvoker(invoker);
        try{
            RPCResponse response = invoker.invoke(invokeParam);
            if (response == null){
               //当调用方式为异步调用:oneWay/future/callback时,调用结果为null
               // 异步调用完成后,需前往RPCThreadSharedContext取future对象,get()最后的结果
                return null;
            }

            //如果调用过程出现错误则抛出异常
            if (response.hasError()){
                response.recycle(); //回收RPCResponse对象
                throw new RPCException(response.getErrorCause(),
                        ExceptionEnum.REMOTE_SERVICE_INVOCATION_FAILED,
                        "REMOTE_SERVICE_INVOCATION_FAILED");
            }
            //调用成功
            return response;
        }catch (RPCException e){
            //调用过程中出现异常,启用集群容错机制
            log.info("正在启动集群容错机制");
            return globalConfig.getClusterConfig().getFaultToleranceHandlerInstance().handle(this, invokeParam, e);
        }
    }

    public List<Invoker> getAvailableInvokers(){
        return new ArrayList<>(protocolInvokers.values());
    }

    private Invoker doselect(List<Invoker> availableInvokers, InvokeParam invokeParam) {
        if (availableInvokers.size() == 0){
            log.error("没有找到可用服务");
            throw new RPCException(ExceptionEnum.NO_SERVER_FOUND, "NO AVAILABLE SERVER NOW");
        }

        if (availableInvokers.size() == 1){
            Invoker invoker = availableInvokers.get(0);
            if (invoker.isAvailable()){
                return invoker;
            }else {
                log.error("找到一个服务,但不可用");
                throw new RPCException(ExceptionEnum.NO_SERVER_FOUND, "NO AVAILABLE SERVER NOW");
            }
        }

        Invoker invoker = globalConfig.getClusterConfig()
                .getLoadBalanceInstance()
                .select(availableInvokers, ((RPCInvokeParam)invokeParam).getRpcRequest());
        if (invoker.isAvailable()){
            return invoker;
        }else {
            availableInvokers.remove(invoker);

            return doselect(availableInvokers, invokeParam);
        }
    }

    public RPCResponse invokeForFaultTolerance(List<Invoker> availableInvokers, InvokeParam invokeParam) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Invoker invoker = doselect(availableInvokers, invokeParam);

        RPCThreadLocalContext.getContext().setInvoker(invoker);

        RPCResponse response = invoker.invoke(invokeParam);
        if (response == null) {
            return null;
        }

        if (response.hasError()) {
            response.recycle(); //回收RPCResponse对象
            throw new RPCException(response.getErrorCause(),
                    ExceptionEnum.REMOTE_SERVICE_INVOCATION_FAILED,
                    "REMOTE_SERVICE_INVOCATION_FAILED");
        }

        return response;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
