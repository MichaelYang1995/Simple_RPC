package com.yqy.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;




/**
 * @ClassName: ProtocolConfig
 * @Description: protocol配置类，主要配置以下属性：
 *               1）协议类型：TCP、HTTP、InJvm
 *               2）端口号
 *               3）Protocol实例
 *               4）Executors实例
 *               5）默认端口号：8000
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ProtocolConfig {

    public static final Integer DEFAULT_PORT = Integer.valueOf(8000);
    private String type;
    private Integer port;

    private Protocol protocolInstance;
    private Executors executors;

    public int getPort(){
        if (port != null){
           return port;
        }
        return DEFAULT_PORT;
    }

    public void close(){
        protocolInstance.close();
        executors.close();
    }
}
