package com.yqy.rpc.transport.api.support;

import com.yqy.rpc.config.GlobalConfig;
import com.yqy.rpc.transport.api.Server;


/**
 * @ClassName: AbstractServer
 * @Description: TODO
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
public abstract class AbstractServer implements Server {
    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig){
        this.globalConfig = globalConfig;
        doInit();
    }

    protected GlobalConfig getGlobalConfig(){
        return globalConfig;
    }

    protected abstract void doInit();
}
