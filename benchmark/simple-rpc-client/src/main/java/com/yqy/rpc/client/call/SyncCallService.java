package com.yqy.rpc.client.call;

import com.yqy.rpc.api.domain.User;
import com.yqy.rpc.api.server.SimpleRPCService;
import com.yqy.rpc.autoconfig.annotation.RPCReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SyncCallService
 * @Description: 同步调用测试
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
@Component
@Slf4j
public class SyncCallService {

    private SimpleRPCService rpcService;    //远程服务的本地代理

    public void syncCallTest(){
        String result = rpcService.helloRPC(new User("pengqidalao"));
        log.info("同步调用结果:{}",result);
    }
}
