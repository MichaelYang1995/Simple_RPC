package com.yqy.rpc.cluster.loadBalance;

import com.yqy.rpc.cluster.api.support.AbstractLoadBalancer;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.protocol.api.Invoker;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName: RandomLoadBalancer
 * @Description: 随机负载均衡算法
 * @Author: YangQingyuan
 * @Date: 2019/12/1 22:44
 * @Version: 1.0
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Invoker doSelect(List<Invoker> availableInvokers, RPCRequest request) {
        return availableInvokers.get(ThreadLocalRandom.current().nextInt(availableInvokers.size()));
    }
}
