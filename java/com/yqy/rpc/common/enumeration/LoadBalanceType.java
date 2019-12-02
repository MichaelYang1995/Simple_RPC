package com.yqy.rpc.common.enumeration;

import com.yqy.rpc.cluster.api.LoadBalancer;
import com.yqy.rpc.common.enumeration.support.ExtensionBaseType;

/**
 * @ClassName: LoadBalanceType
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/1 20:51
 * @Version: 1.0
 */
public enum LoadBalanceType implements ExtensionBaseType<LoadBalancer> {
    CONSISTENTHASH(new ConsistentHashLoadBalancer()),    //一致性哈希
    LEASTACTIVE(new LeastActiveLoadBalancer()),          //最小活跃度
    RANDOM(new RandomLoadBalancer()),                    //随机
    WEIGHTEDRANDOM(new WeightedRandomLoadBalancer()),    //加权随机
    ROUNDROBIN(new RoundRobinLoadBalancer());            //轮询

    private LoadBalancer loadBalancer;

    LoadBalanceType(LoadBalancer loadBalancer){
        this.loadBalancer = loadBalancer;
    }

    @Override
    public LoadBalancer getInstance() {
        return loadBalancer;
    }
}
