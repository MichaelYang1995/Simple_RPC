package com.yqy.rpc.cluster.faultTolerance;

import com.yqy.rpc.cluster.api.FaultToleranceHandler;
import com.yqy.rpc.cluster.api.support.ClusterInvoker;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: FailFastFaultToleranceHandler
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/18
 * @Version: V1.0
 **/
@Slf4j
public class FailSafeFaultToleranceHandler implements FaultToleranceHandler {

    @Override
    public RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam, RPCException e) {
        log.info("调用出现异常(集群容错:failsafe),requestID:{}",invokeParam.getRequestID());
        throw e;
    }
}
