package com.yqy.rpc.cluster.faultTolerance;

import com.yqy.rpc.cluster.api.FaultToleranceHandler;
import com.yqy.rpc.cluster.api.support.ClusterInvoker;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: FailFastFaultToleranceHandler
 * @Description: 集群容错机制:快速失败
 *               RPC调用失败后立即报错且不再重试,通常用于非幂等性的写操作,如新增记录
 * @Author: YangQingyuan
 * @Data: 2019/11/18
 * @Version: V1.0
 **/
@Slf4j
public class FailFastFaultToleranceHandler implements FaultToleranceHandler {
    @Override
    public RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam, RPCException e) {
        log.info("调用出现异常(集群容错:failfast),requestID:{}",invokeParam.getRequestID());
        throw e;
    }
}
