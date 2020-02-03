package com.yqy.rpc.cluster.loadBalance;

import com.yqy.rpc.cluster.api.support.AbstractLoadBalancer;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.protocol.api.Invoker;

import java.util.List;

/**
 * @ClassName: RoundRobinLoadBalancer
 * @Description: 轮询负载均衡算法
 * @Author: YangQingyuan
 * @Date: 2019/12/1 22:48
 * @Version: 1.0
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private int index = 0;  //轮询负载均衡算法中,当前应当被选择的服务器索引
    @Override
    protected Invoker doSelect(List<Invoker> availableInvokers, RPCRequest request) {
        //定义为同步方法,保证index的正确性,防止多线程同时请求轮询时并发更新index
        Invoker invoker = availableInvokers.get(index);
        index = (index+1)%availableInvokers.size();
        return invoker;
    }
}
