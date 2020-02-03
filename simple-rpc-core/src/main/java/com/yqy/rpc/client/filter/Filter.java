package com.yqy.rpc.client.filter;

import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.protocol.api.InvokeParam;
import com.yqy.rpc.protocol.api.Invoker;

/**
 * @ClassName: Filter
 * @Description: 过滤器接口
 * @Author: YangQingyuan
 * @Data: 2019/11/28
 * @Version: V1.0
 **/
public interface Filter {
    /**
     * 过滤器Invoke方法,实际上并不处理业务逻辑,只是在真正的invoke前后增加一些处理
     * @param invoker
     * @param invokeParam
     * @return
     */
    RPCResponse invoke(Invoker invoker, InvokeParam invokeParam);
}
