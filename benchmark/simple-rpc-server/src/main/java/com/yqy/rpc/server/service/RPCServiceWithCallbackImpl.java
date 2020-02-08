package com.yqy.rpc.server.service;

import com.yqy.rpc.api.callback.CallbackInterface;
import com.yqy.rpc.api.domain.User;
import com.yqy.rpc.api.server.RPCServiceWithCallback;
import com.yqy.rpc.autoconfig.annotation.RPCService;
import org.springframework.stereotype.Component;

/**
 * @ClassName: RPCServiceWithCallbackImpl
 * @Description: 带回调参数的服务接口实现类
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
@RPCService(interfaceClass = RPCServiceWithCallback.class,callback = true,callbackMethod = "getInfoFromClient")
public class RPCServiceWithCallbackImpl implements RPCServiceWithCallback {
    @Override
    public void hello(User user, CallbackInterface callbackInterface) {
        String result = "i am RPCServer,i'm calling "+user.getUserName()+" back...";
        callbackInterface.getInfoFromClient(result);
    }
}
