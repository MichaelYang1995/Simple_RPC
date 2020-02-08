package com.yqy.rpc.client.call;

import com.yqy.rpc.api.domain.User;
import com.yqy.rpc.api.server.SimpleRPCService;
import com.yqy.rpc.autoconfig.annotation.RPCReference;
import com.yqy.rpc.common.context.RPCThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @ClassName: AsyncCallService
 * @Description: 异步调用测试
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
@Component
@Slf4j
public class AsyncCallService {
    @RPCReference(async = true)
    private SimpleRPCService service;

    public void asyncCall() throws Exception{
        User user = new User("zxq");
        service.helloRPC(user);
        Future<String> future = RPCThreadLocalContext.getContext().getFuture();
        log.info("异步调用结果:{}",future.get());
    }
}
