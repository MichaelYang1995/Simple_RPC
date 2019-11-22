package com.yqy.rpc.protocol.api.support;

import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.config.ReferenceConfig;
import com.yqy.rpc.protocol.api.InvokeParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: RPCInvokeParam
 * @Description: 封装RPC请求对象和引用配置对象的类
 * @Author: YangQingyuan
 * @Data: 2019/11/19
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RPCInvokeParam implements InvokeParam {
    private RPCRequest rpcRequest;

    private ReferenceConfig referenceConfig;

    @Override
    public String getInterfaceName() {
        return rpcRequest.getInterfaceName();
    }

    @Override
    public String getMethodName() {
        return rpcRequest.getMethodName();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return rpcRequest.getParameterTypes();
    }

    @Override
    public Object[] getParameter() {
        return rpcRequest.getParameters();
    }

    @Override
    public String getRequestID() {
        return rpcRequest.getRequestID();
    }

    @Override
    public String ToString(){
        return "RPCInvokeParam{"+
                rpcRequest+
                "}";
    }
}
