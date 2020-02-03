package com.pq.rpc.autoconfig;

import com.pq.rpc.autoconfig.beanPostProcessor.RPCConsumerBeanPostProcessor;
import com.pq.rpc.autoconfig.beanPostProcessor.RPCProviderBeanPostProcessor;
import com.yqy.rpc.cluster.api.FaultToleranceHandler;
import com.yqy.rpc.cluster.api.LoadBalancer;
import com.yqy.rpc.cluster.api.support.AbstractLoadBalancer;
import com.yqy.rpc.cluster.faultTolerance.FailOverFaultToleranceHandler;
import com.yqy.rpc.common.ExtensionLoader;
import com.yqy.rpc.common.enumeration.*;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.config.*;
import com.yqy.rpc.executor.api.TaskExecutor;
import com.yqy.rpc.protocol.api.support.AbstractProtocol;
import com.yqy.rpc.proxy.api.RPCProxyFactory;
import com.yqy.rpc.registry.zookeeper.ZKServiceRegistry;
import com.yqy.rpc.serialize.api.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName: RPCAutoConfiguration
 * @Description: springboot自动配置类
 *               simple-rpc-spring-boot-starter作为第三方依赖配置到项目中时,该类可以自动配置所有bean并注册到spring容器中供应用程序使用
 * @Author: YangQingyuan
 * @Date: 2019/12/2 11:32
 * @Version: 1.0
 */
@Slf4j
public class RPCAutoConfiguration implements InitializingBean {
    /**
     * 创建配置类/扩展点实例的工具类
     * @throws Exception
     */
    private ExtensionLoader extensionLoader;

    @Autowired
    RPCProperties rpcProperties;

    @Bean(initMethod = "init", destroyMethod = "close")
    public RegistryConfig registryConfig(){
        RegistryConfig registryConfig = rpcProperties.getRegistryConfig();
        if (registryConfig == null){
            throw new RPCException(ExceptionEnum.SIMPLE_RPC_CONFIG_ERROR, "必须配置registry");
        }
        //注入依赖
        registryConfig.setRegistryInstance(new ZKServiceRegistry(registryConfig));
        log.info("registryConfig:{}", registryConfig);
        return registryConfig;
    }

    @Bean
    public ApplicationConfig applicationConfig(){
        ApplicationConfig applicationConfig = rpcProperties.getApplicationConfig();
        if (applicationConfig == null){
            throw new RPCException(ExceptionEnum.SIMPLE_RPC_CONFIG_ERROR, "必须配置application");
        }
        //注入依赖
        applicationConfig.setProxyFactoryInstance(extensionLoader.load(RPCProxyFactory.class, ProxyFactoryType.class, applicationConfig.getProxyFactoryName()));
        applicationConfig.setSerializerInstance(extensionLoader.load(Serializer.class, SerializerType.class, applicationConfig.getSerializer()));
        log.info("applicationConfig:{}", applicationConfig);
        return applicationConfig;
    }

    @Bean(destroyMethod = "close")
    public ProtocolConfig protocolConfig(ApplicationConfig applicationConfig, RegistryConfig registryConfig, ClusterConfig clusterConfig){
        ProtocolConfig protocolConfig = rpcProperties.getProtocolConfig();
        if (protocolConfig == null){
            throw new RPCException(ExceptionEnum.SIMPLE_RPC_CONFIG_ERROR, "必须配置protocol");
        }

        AbstractProtocol protocol = extensionLoader.load(AbstractProtocol.class, ProtocolType.class, protocolConfig.getType());
        protocol.init(GlobalConfig.builder().
                protocolConfig(protocolConfig).
                applicationConfig(applicationConfig).
                registryConfig(registryConfig).
                clusterConfig(clusterConfig).
                build());
        protocolConfig.setProtocolInstance(protocol);

        ((AbstractLoadBalancer)clusterConfig.getLoadBalanceInstance()).
                updateGlobalConfig(GlobalConfig.
                        builder().
                        protocolConfig(protocolConfig).
                        build());

        //配置Executor
        Executors executors = protocolConfig.getExecutors();
        if (executors!=null){
            ExecutorConfig clientExecutorConfig = executors.getClient();
            if (clientExecutorConfig!=null){
                TaskExecutor executor = extensionLoader.load(TaskExecutor.class, ExecutorType.class, clientExecutorConfig.getType());
                executor.init(clientExecutorConfig.getThreads());
                clientExecutorConfig.setTaskExecutorInstance(executor);
            }
            ExecutorConfig serverExecutorConfig = executors.getServer();
            if (serverExecutorConfig!=null){
                TaskExecutor executor = extensionLoader.load(TaskExecutor.class, ExecutorType.class, serverExecutorConfig.getType());
                executor.init(serverExecutorConfig.getThreads());
                serverExecutorConfig.setTaskExecutorInstance(executor);
            }
        }
        log.info("protocolConfig:{}", protocolConfig);
        return protocolConfig;
    }

    @Bean
    public ClusterConfig clusterConfig(ApplicationConfig applicationConfig, RegistryConfig registryConfig){
        ClusterConfig clusterConfig = rpcProperties.getClusterConfig();
        if (clusterConfig==null){
            throw new RPCException(ExceptionEnum.SIMPLE_RPC_CONFIG_ERROR, "必须配置cluster");
        }
        //注入依赖
        if (clusterConfig.getFaulttolerance()!=null){
            clusterConfig.setFaultToleranceHandlerInstance(extensionLoader.load(
                    FaultToleranceHandler.class, FaultToleranceHandlerType.class, clusterConfig.getFaulttolerance()
            ));
        }else {
            //默认的集群容错机制为failover
            clusterConfig.setFaultToleranceHandlerInstance(new FailOverFaultToleranceHandler());
        }
        if (clusterConfig.getLoadbalance()!=null){
            clusterConfig.setLoadBalanceInstance(extensionLoader.load(
                    LoadBalancer.class, LoadBalanceType.class, clusterConfig.getLoadbalance()
            ));
        }

        ((AbstractLoadBalancer)clusterConfig.getLoadBalanceInstance()).updateGlobalConfig(GlobalConfig.
                builder().
                applicationConfig(applicationConfig).
                registryConfig(registryConfig).
                clusterConfig(clusterConfig).
                build());
        log.info("clusterConfig:{}", clusterConfig);
        return clusterConfig;
    }

    @Bean
    public RPCConsumerBeanPostProcessor rpcConsumerBeanPostProcessor(ApplicationConfig applicationConfig,
                                                                     RegistryConfig registryConfig,
                                                                     ProtocolConfig protocolConfig,
                                                                     ClusterConfig clusterConfig){
        RPCConsumerBeanPostProcessor processor = new RPCConsumerBeanPostProcessor();
        processor.init(applicationConfig, registryConfig, protocolConfig, clusterConfig);
        log.info("客户端Bean后置处理器初始化完成");
        return processor;
    }

    @Bean
    public RPCProviderBeanPostProcessor rpcProviderBeanPostProcessor(ApplicationConfig applicationConfig,
                                                                     RegistryConfig registryConfig,
                                                                     ProtocolConfig protocolConfig,
                                                                     ClusterConfig clusterConfig){
        RPCProviderBeanPostProcessor processor = new RPCProviderBeanPostProcessor();
        processor.init(applicationConfig, registryConfig, protocolConfig, clusterConfig);
        log.info("服务端Bean后置处理器初始化完成");
        return processor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.extensionLoader = ExtensionLoader.getInstance();
        //加载配置文件,实例化扩展点实现类并注册到一个map中
        extensionLoader.loadResource();
    }
}
