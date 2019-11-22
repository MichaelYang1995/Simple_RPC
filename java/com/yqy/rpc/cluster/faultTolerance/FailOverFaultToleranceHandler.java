package com.yqy.rpc.cluster.faultTolerance;

import com.yqy.rpc.cluster.api.FaultToleranceHandler;
import com.yqy.rpc.cluster.api.support.ClusterInvoker;
import com.yqy.rpc.common.constant.ClusterConstant;
import com.yqy.rpc.common.context.RPCThreadLocalContext;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FailFastFaultToleranceHandler
 * @Description: 集群容错机制:自动切换
 *               RPC调用在一台服务器上出现异常后,快速将请求切换到别的服务器上,通常用于读操作
 * @Author: YangQingyuan
 * @Data: 2019/11/18
 * @Version: V1.0
 **/
@Slf4j
public class FailOverFaultToleranceHandler implements FaultToleranceHandler {
    @Override
    public RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam, RPCException e) {
        //取出当前线程对应的Invoker,这是出现异常的Invoker,需从服务列表中删除
        Invoker failInvoker = RPCThreadLocalContext.getContext().getInvoker();
        Map<String, Invoker> excludeInvokers = new HashMap<>();
        excludeInvokers.put(failInvoker.getServiceURL().getServiceAddress(),failInvoker);
        for (int i = 0; i < ClusterConstant.FAILOVER_RETRY_TIMES;i++){
            List<Invoker> availableInvokers = clusterInvoker.getAvailableInvokers();
            Iterator<Invoker> iter = availableInvokers.iterator();
            while (iter.hasNext()){
                if (excludeInvokers.containsKey(iter.next().getServiceURL().getServiceAddress())){
                    iter.remove();
                }
            }

            if (availableInvokers.size() == 0){
                //没有可用服务了
                e.printStackTrace();
                throw new RPCException(ExceptionEnum.NO_AVAILABLE_SERVICE, "NO_AVAILABLE_SERVICE");
            }

            try{
                log.info("正在重试......");
                return clusterInvoker.invokeForFaultTolerance(availableInvokers, invokeParam);
            }catch (Exception e1){
                e1.printStackTrace();
                log.info("第{}次重试失败...", i+1);
                Invoker fInvoker = RPCThreadLocalContext.getContext().getInvoker();
                //将失败的Invoker放入失败队列
                excludeInvokers.put(fInvoker.getServiceURL().getServiceAddress(), fInvoker);
            }
        }
        //指定次数内,依然没有成功
        throw new RPCException(ExceptionEnum.EXCEEDED_RETRIES, "EXCEEDED_RETRIES");
    }
}
