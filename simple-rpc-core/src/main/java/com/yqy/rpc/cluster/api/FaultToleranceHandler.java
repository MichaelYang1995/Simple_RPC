package com.yqy.rpc.cluster.api;

import com.yqy.rpc.cluster.api.support.ClusterInvoker;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.protocol.api.InvokeParam;

/**
 * @ClassName: FaultToleranceHandler
 * @Description: 集群容错处理器:调用失败后的处理机制
 *               常见的处理机制有：failover(失败自动切换)、failfast(快速失败)、failsafe(安全失败)等
 *               注意:容错机制仅对同步调用有效，因为异步调用的response都是直接返回null
 * @Author: YangQingyuan
 * @Data: 2019/11/18
 * @Version: V1.0
 **/
public interface FaultToleranceHandler {
    /**
     * 集群容错处理方法,配置不同的容错机制，会有不同的实现
     * @param clusterInvoker
     * @param invokeParam
     * @param e
     * @return
     */
    RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam, RPCException e);
}
