package com.yqy.rpc.config;

import com.yqy.rpc.client.filter.Filter;
import com.yqy.rpc.common.domain.GlobalRecycler;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.support.AbstractConfig;
import com.yqy.rpc.protocol.api.Invoker;
import com.yqy.rpc.protocol.api.support.RPCInvokeParam;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ReferenceConfig
 * @Description: 远程服务引用配置类，主要配置以下属性：
 *               1）服务接口名
 *               2）服务接口类类型
 *               3）是否异步调用（默认同步调用）
 *               4）是否以oneway（单向）的形式调用服务，即发送请求不等待应答，也没有回调函数触发等（适用于“日记手机”等可靠性要求不高，不需要回调函数的情况）
 *               5）是否以callback的形式来调用服务
 *               6）回调方法名
 *               7）回调参数索引，默认为1
 *               8）超时时间，默认为3000ms
 *               9）是否泛化调用服务
 *              10）过滤器链表filters
 *              11）远程服务抽象调用者invoker
 *              12）远程服务的本地代理对象ref
 *              13）是否初始化（判断该referenceConfig对象是否已经被初始化）
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
@Data
@Builder
@Slf4j
public class ReferenceConfig<T> extends AbstractConfig {
    private String interfaceName;
    private Class<T> interfaceClass;
    private boolean isAsync;
    private boolean isOneWay;
    private boolean isCallback;
    private String callbackMethod;
    private int callbackParamIndex = 1;
    private long timeout = 3000;
    private boolean isGeneric;
    private List<Filter> filters;

    /**
     * 同一个服务的引用是有缓存的，可能一个服务同时被多个进程引用，此时它们共用同一个invoker，
     * 因此需要添加volatile描述符，保证内存可见性
     */
    private volatile Invoker<T> invoker;

    /**
     * 原因同上
     */
    private volatile T ref;

    /**
     * 只要引用中的Invoker和ref被创建，即已初始化，因此initialized同样需要volatile修饰
     */
    private volatile boolean initialized;

    /**
     * 引用配置类的本地缓存，引用同一个服务时共享缓存中的同一个ReferenceConfig
     */
    private static final Map<String, ReferenceConfig<?>> CACHE = new ConcurrentHashMap<>();

    public static <T> ReferenceConfig<T> createReferenceConfig(String interfaceName,
                                                               /**参数可控*/
                                                               Class<T> interfaceClass,
                                                               boolean isAsync,
                                                               boolean isCallback,
                                                               boolean isOneWay,
                                                               long timeout,
                                                               String callbackMethod,
                                                               int callbackParamIndex,
                                                               boolean isGeneric,
                                                               List<Filter> filters){
        if (CACHE.containsKey(interfaceName)){
            if (CACHE.get(interfaceName).isDiff(isAsync, isCallback, isOneWay, timeout, callbackMethod, callbackParamIndex, isGeneric, filters)){
                throw new RPCException(ExceptionEnum.SAME_SERVICE_CONNOT_BE_REFERRED_BY_DIFFERENT_CONFIG,
                        "同一个服务在同一客户端只能以相同的配置被引用："+interfaceName);
            }
            return (ReferenceConfig<T>) CACHE.get(interfaceName);
        }

        ReferenceConfig referenceConfig = ReferenceConfig.builder()
                .interfaceName(interfaceName)
                .interfaceClass((Class<Object>)interfaceClass)
                .isAsync(isAsync)
                .isCallback(isCallback)
                .isOneWay(isOneWay)
                .timeout(timeout)
                .callbackMethod(callbackMethod)
                .callbackParamIndex(callbackParamIndex)
                .isGeneric(isGeneric)
                .filters(filters != null ? filters : new ArrayList<>()).build();
        CACHE.put(interfaceName,referenceConfig);

        return referenceConfig;
    }

    private boolean isDiff(boolean isAsync,
                          boolean isCallback,
                          boolean isOneWay,
                          long timeout,
                          String callbackMethod,
                          int callbackParamIndex,
                          boolean isGeneric,
                          List<Filter> filters){
        if (this.isAsync != isAsync){
            return true;
        }

        if (this.isCallback != isCallback){
            return true;
        }

        if (this.isOneWay != isOneWay) {
            return true;
        }
        if (this.timeout != timeout) {
            return true;
        }
        if (!this.callbackMethod.equals(callbackMethod)) {
            return true;
        }
        if (this.callbackParamIndex != callbackParamIndex) {
            return true;
        }
        if (this.isGeneric != isGeneric) {
            return true;
        }
        return false;
    }

    private void init(){
        if (initialized){
            return;
        }
        initialized = true;
        //获取ClusterInvoker，集群层面的Invoker
        invoker = getClusterConfig().getLoadBalanceInstance().referCluster(this);
        //判断是否为泛化调用
        if (!isGeneric){
            //如为非泛化调用，则根据ClusterInvoker调用代理工厂生产代理实例
            ref = getApplicationConfig().getProxyFactoryInstance().createProxy(invoker);
        }
    }

    public Object invokeforGeneric(String methodname, Class<?>[] parameterType, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (!initialized){
            init();
        }
        if (isGeneric){
            RPCRequest request = GlobalRecycler.reuse(RPCRequest.class);
            log.info("泛化调用服务：{} {}", interfaceName, methodname);
            request.setInterfaceName(interfaceName);
            request.setMethodName(methodname);
            request.setParameterTypes(parameterType);
            request.setParameters(args);
            request.setRequestID(UUID.randomUUID().toString());

            RPCInvokeParam invokeParam = RPCInvokeParam.builder()
                    .rpcRequest(request)
                    .referenceConfig(this)
                    .build();
            RPCResponse response = invoker.invoke(invokeParam);
            if (response == null) {
                // callback,oneway,async
                return null;
            } else {
                return response.getResult();
            }
        } else{
          throw new RPCException(ExceptionEnum.GENERIC_INVOCATION_ERROR ,"只有泛化调用的reference可以调用invoke");
        }
    }

    public T get(){
        if (!initialized){
            init();
        }
        return ref;
    }

    public static ReferenceConfig getReferenceConfigByInterfaceName(String interfaceName){
        return CACHE.get(interfaceName);
    }

    @Override
    public String toString() {
        return "ReferenceConfig{" +
                "interfaceName='" + interfaceName + '\'' +
                ", interfaceClass=" + interfaceClass +
                ", isAsync=" + isAsync +
                ", isOneWay=" + isOneWay +
                ", isCallback=" + isCallback +
                ", timeout=" + timeout +
                ", callbackMethod='" + callbackMethod + '\'' +
                ", callbackParamIndex=" + callbackParamIndex +
                ", ref=" + ref +
                ", initialized=" + initialized +
                ", filters=" + filters +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceConfig<?> that = (ReferenceConfig<?>) o;
        return isAsync == that.isAsync &&
                isOneWay == that.isOneWay &&
                isCallback == that.isCallback &&
                timeout == that.timeout &&
                callbackParamIndex == that.callbackParamIndex &&
                initialized == that.initialized &&
                Objects.equals(interfaceName, that.interfaceName) &&
                Objects.equals(interfaceClass, that.interfaceClass) &&
                Objects.equals(callbackMethod, that.callbackMethod) &&
                Objects.equals(ref, that.ref) &&
                Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interfaceName, interfaceClass, isAsync, isOneWay, isCallback, timeout, callbackMethod, callbackParamIndex, ref, initialized, filters);
    }
}
