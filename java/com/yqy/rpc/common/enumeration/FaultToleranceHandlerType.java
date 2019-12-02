package com.yqy.rpc.common.enumeration;

import com.yqy.rpc.cluster.api.FaultToleranceHandler;
import com.yqy.rpc.cluster.faultTolerance.FailFastFaultToleranceHandler;
import com.yqy.rpc.cluster.faultTolerance.FailOverFaultToleranceHandler;
import com.yqy.rpc.cluster.faultTolerance.FailSafeFaultToleranceHandler;
import com.yqy.rpc.common.enumeration.support.ExtensionBaseType;

/**
 * @ClassName: FaultToleranceHandlerType
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/1 20:32
 * @Version: 1.0
 */
public enum FaultToleranceHandlerType implements ExtensionBaseType<FaultToleranceHandler> {
    FAILOVER(new FailOverFaultToleranceHandler()),     //失败切换
    FAILFAST(new FailFastFaultToleranceHandler()),     //快速失败
    FAILSAFE(new FailSafeFaultToleranceHandler());     //安全失败

    private FaultToleranceHandler faultToleranceHandler;

    FaultToleranceHandlerType(FaultToleranceHandler faultToleranceHandler){
        this.faultToleranceHandler = faultToleranceHandler;
    }

    @Override
    public FaultToleranceHandler getInstance() {
        return faultToleranceHandler;
    }
}
