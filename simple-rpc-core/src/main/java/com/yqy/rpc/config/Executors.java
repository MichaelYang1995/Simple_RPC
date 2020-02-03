package com.yqy.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Executors
 * @Description: 服务端与客户端的通用executor配置类，配置以下属性：
 *               1）隶属于provider的ExecutorConfig--server
 *               2）隶属于consumer的ExecutorConfig--client
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Executors {
    private ExecutorConfig server;

    private ExecutorConfig client;

    public void close(){
        if(server!=null){
            server.close();
        }
        if(client!=null){
            client.close();
        }
    }
}
