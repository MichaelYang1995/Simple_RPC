package com.yqy.rpc.api.server;

import com.yqy.rpc.api.domain.User;

/**
 * @ClassName: SimpleRPCService
 * @Description: 简单RPC服务接口
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
public interface SimpleRPCService {
    public String helloRPC(User user);
}
