package com.yqy.rpc.client.filter.impl;

import com.yqy.rpc.client.filter.Filter;
import com.yqy.rpc.common.context.RPCStatus;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.protocol.api.InvokeParam;
import com.yqy.rpc.protocol.api.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName: ActivityStatisticsFilter
 * @Description: 统计RPC调用活跃度的过滤器
 * @Author: YangQingyuan
 * @Data: 2019/11/28
 * @Version: V1.0
 **/
@Slf4j
public class ActivityStatisticsFilter implements Filter {
    @Override
    public RPCResponse invoke(Invoker invoker, InvokeParam invokeParam) {
        RPCResponse response = null;
        try {
            log.info("start RPC...");
            //调用开始,活跃度+1
            RPCStatus.inActivity(
                    invoker.getInterfaceName(),
                    invokeParam.getMethodName(),
                    invoker.getServiceURL().getServiceAddress());
            response = invoker.invoke(invokeParam);  //调用下一个filter,或者真正的invoker
        }catch (RPCException e){
            log.info("Catch a RPC Exception...");
            //调用失败, 活跃度-1
            RPCStatus.deActivity(
                    invoker.getInterfaceName(),
                    invokeParam.getMethodName(),
                    invoker.getServiceURL().getServiceAddress());
            throw e;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //调用顺利完成,活跃度-1
        RPCStatus.deActivity(
                invoker.getInterfaceName(),
                invokeParam.getMethodName(),
                invoker.getServiceURL().getServiceAddress());

        return response;
    }
}
