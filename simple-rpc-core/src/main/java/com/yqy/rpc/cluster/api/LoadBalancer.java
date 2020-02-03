package com.yqy.rpc.cluster.api;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.protocol.api.Invoker;

import java.util.List;

/**
 * @ClassName: LoadBalancer
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/18
 * @Version: V1.0
 **/
public interface LoadBalancer {
     <T> Invoker<T> referCluster(ReferenceConfig<T> referenceConfig);

    Invoker select(List<Invoker> availableInvokers, RPCRequest request);
}
