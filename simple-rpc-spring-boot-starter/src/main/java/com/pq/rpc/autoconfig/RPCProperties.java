package com.pq.rpc.autoconfig;
import com.yqy.rpc.config.ApplicationConfig;
import com.yqy.rpc.config.ClusterConfig;
import com.yqy.rpc.config.ProtocolConfig;
import com.yqy.rpc.config.RegistryConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: RPCProperties
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/2 11:33
 * @Version: 1.0
 */
@ConfigurationProperties(prefix = "rpc")
@Data
public class RPCProperties {
    private ApplicationConfig applicationConfig;

    private ProtocolConfig protocolConfig;

    private ClusterConfig clusterConfig;

    private RegistryConfig registryConfig;
}
