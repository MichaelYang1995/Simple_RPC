package com.yqy.rpc.cluster.loadBalance;

import com.yqy.rpc.cluster.api.support.AbstractLoadBalancer;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.protocol.api.Invoker;
import com.yqy.rpc.registry.api.ServiceURL;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName: WeightedRandomLoadBalancer
 * @Description: 加权随机负载均衡算法
 * @Author: YangQingyuan
 * @Date: 2019/12/1 22:50
 * @Version: 1.0
 */
public class WeightedRandomLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Invoker doSelect(List<Invoker> availableInvokers, RPCRequest request) {
        int weightSum = 0; //权重和
        //计算权重
        for (Invoker invoker:availableInvokers){
            weightSum += Integer.parseInt(invoker.getServiceURL().getParamsByKey(ServiceURL.Key.WEIGHT).get(0));
        }
        int randomValue = ThreadLocalRandom.current().nextInt(weightSum);
        for (Invoker invoker:availableInvokers){
            randomValue -= Integer.parseInt(invoker.getServiceURL().getParamsByKey(ServiceURL.Key.WEIGHT).get(0));
            if (randomValue < 0){
                return invoker;
            }
        }
        return null;
    }
}
