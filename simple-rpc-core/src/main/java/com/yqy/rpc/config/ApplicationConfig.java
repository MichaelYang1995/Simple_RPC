package com.yqy.rpc.config;

import com.yqy.rpc.proxy.api.RPCProxyFactory;
import com.yqy.rpc.serialize.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @ClassName: ApplicationConfig
 * @Description: 项目应用配置类，主要配置以下属性：
 *               1）应用名称
 *               2）序列化算法：Jdk、Protostuff
 *               3）代理对象工厂
 *               4）序列化算法实例
 *               5）代理对象工厂实例
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfig {
    private String name;
    private String serializer;
    private String proxyFactoryName;

    private Serializer serializerInstance;
    private RPCProxyFactory proxyFactoryInstance;
}
