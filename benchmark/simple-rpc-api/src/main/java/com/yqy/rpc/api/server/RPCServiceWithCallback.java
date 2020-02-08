package com.yqy.rpc.api.server;

import com.yqy.rpc.api.callback.CallbackInterface;
import com.yqy.rpc.api.domain.User;

/**
 * @ClassName: RPCServiceWithCallback
 * @Description: 带回调参数的服务接口
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
public interface RPCServiceWithCallback {
    void hello(User user, CallbackInterface callbackInterface);
}
