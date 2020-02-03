package com.yqy.rpc.cluster.loadBalance;

import com.yqy.rpc.cluster.api.support.AbstractLoadBalancer;
import com.yqy.rpc.common.context.RPCStatus;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.protocol.api.Invoker;

import java.util.List;

/**
 * @ClassName: LeastActiveLoadBalancer
 * @Description: 最小活跃度负载均衡算法
 * @Author: YangQingyuan
 * @Date: 2019/12/1 22:29
 * @Version: 1.0
 */
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Invoker doSelect(List<Invoker> availableInvokers, RPCRequest request) {
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Invoker target = null;
        int leastActivity = 0;
        for (Invoker invoker:availableInvokers){
            int curActivity = RPCStatus.getActivity(
                    interfaceName,
                    methodName,
                    invoker.getServiceURL().getServiceAddress());
            if (target == null || curActivity < leastActivity){
                target = invoker;
                leastActivity = curActivity;
            }
        }
        return target;
    }
}
