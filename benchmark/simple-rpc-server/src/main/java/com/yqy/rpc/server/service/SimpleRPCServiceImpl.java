package com.yqy.rpc.server.service;

import com.yqy.rpc.api.domain.User;
import com.yqy.rpc.api.server.SimpleRPCService;
import com.yqy.rpc.autoconfig.annotation.RPCService;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SimpleRPCServiceImpl
 * @Description: 简单的服务接口实现类
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
@RPCService(interfaceClass = SimpleRPCService.class)
public class SimpleRPCServiceImpl implements SimpleRPCService {
    @Override
    public String helloRPC(User user) {
        return "remote_call success!";
    }
}
