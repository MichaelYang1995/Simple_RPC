package com.yqy.rpc.cluster.loadBalance;

import com.yqy.rpc.cluster.api.support.AbstractLoadBalancer;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.protocol.api.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @ClassName: ConsistentHashLoadBalancer
 * @Description: 一致性哈希负载均衡算法
 * @Author: YangQingyuan
 * @Date: 2019/12/2 10:18
 * @Version: 1.0
 */
@Slf4j
public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {
    private MessageDigest md5 = null;

    private TreeMap<Long, Invoker> hashCirle = new TreeMap<>(); //哈希环

    private static final int REPLICA_NODE_NUM = 160;            //虚拟节点数(虚拟节点用来保证分布均匀性)

    private List<Invoker> cachedInvokers;                       //之前的可用服务列表

    @Override
    protected Invoker doSelect(List<Invoker> availableInvokers, RPCRequest request) {
        if (cachedInvokers==null || availableInvokers.hashCode()!=cachedInvokers.hashCode()){
            //构建哈希环
            buildHashCirle(availableInvokers);
        }
        if (availableInvokers.size()==0){
            return null;
        }

        //生成请求对象的哈希值
        long hash = hash(md5(request.key()), 0);

        if (!hashCirle.containsKey(hash)){
            //一般情况,请求对象的哈希值不与哈希环中任一节点的值相同
            SortedMap<Long, Invoker> tailMap = hashCirle.tailMap(hash);    //tailMap为比hash值大的节点集合
            //如果tailMap为空,说明hash值大于哈希环中最大的值,选择哈希环开头的invoker
            //如果不为空,则直接选择tailMap中开头的invoker
            hash = tailMap.isEmpty()?hashCirle.firstKey():tailMap.firstKey();
        }
        //如果请求对象的hash值正好与某个invoker的哈希值一致,则直接返回对应的invoker
        return hashCirle.get(hash);
    }

    /**
     * 根据可用invoker列表构建(重建)哈希环
     * @param availableInvokers
     */
    private void buildHashCirle(List<Invoker> availableInvokers){
        if (cachedInvokers==null){
            //第一次建环
            for (Invoker invoker:availableInvokers){
                add(invoker);
            }
            cachedInvokers = availableInvokers;
            return;
        }
        log.info("旧服务器列表:{}", cachedInvokers);
        log.info("新服务器列表:{}", availableInvokers);
        for (Invoker invoker:availableInvokers){
            if (!cachedInvokers.contains(invoker)){
                add(invoker);
            }
        }
        for (Invoker invoker:cachedInvokers){
            if (!availableInvokers.contains(invoker)){
                remove(invoker);
            }
        }
        cachedInvokers = availableInvokers;
    }

    /**
     * 添加一个服务器节点
     * @param invoker
     */
    private void add(Invoker invoker){
        for (int i=0;i<REPLICA_NODE_NUM/4;i++){
            byte[] bytes = md5(invoker.getServiceURL().getServiceAddress()+i);
            for (int j=0;j<4;j++){
                long m = hash(bytes, j);
                hashCirle.put(m, invoker);
            }
        }
    }

    /**
     * 删除一个服务节点
     * @param invoker
     */
    private void remove(Invoker invoker){
        for (int i=0;i<REPLICA_NODE_NUM/4;i++){
            byte[] bytes = md5(invoker.getServiceURL().getServiceAddress() + i);
            for (int j=0;j<4;j++){
                long m = hash(bytes, j);
                hashCirle.remove(m, invoker);
            }
        }
    }


    /**
     * 使用MD5算法得到长度为16的字节数组,保证hash函数的平衡性(分布均匀)
     * @param key  键
     * @return     值
     */
    private byte[] md5(String key) {
        if (md5==null){
            try{
                md5 = MessageDigest.getInstance("MD5");
            }catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
        }
        md5.reset();
        md5.update(key.getBytes());
        return md5.digest();
    }

    /**
     * 将128位的md5散列字节数组转换成long型整数
     * num==0时,是取出bytes的低32位,num==3时,是取出bytes的高32位
     * @param bytes
     * @param num
     * @return
     */
    private long hash(byte[] bytes, int num){
        return ((long)(bytes[3+num*4] & 0xFF) << 24)
                |((long)(bytes[2+num*4] & 0xFF) << 16)
                |((long)(bytes[1+num*4] & 0xFF) << 8)
                |((long)(bytes[num*4] & 0xFF))
                & 0xFFFFFFFFL;
    }
}
