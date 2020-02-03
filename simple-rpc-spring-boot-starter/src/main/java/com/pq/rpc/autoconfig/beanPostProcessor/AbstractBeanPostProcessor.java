package com.pq.rpc.autoconfig.beanPostProcessor;

import com.yqy.rpc.config.*;
import com.yqy.rpc.config.support.AbstractConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @ClassName: AbstractBeanPostProcessor
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/2 11:36
 * @Version: 1.0
 */
public abstract class AbstractBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private GlobalConfig globalConfig;

    protected ApplicationContext ac;   //spring容器

    public void init(ApplicationConfig applicationConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig, ClusterConfig clusterConfig){
        globalConfig = GlobalConfig.builder().
                applicationConfig(applicationConfig).
                registryConfig(registryConfig).
                protocolConfig(protocolConfig).
                clusterConfig(clusterConfig).build();
    }

    //将初始化后的全局配置类配置到特定配置类中
    protected void initConfig(AbstractConfig config){
        config.init(globalConfig);
    }

    public static void initConfig(ApplicationContext ctx, AbstractConfig config){
        config.init(
                GlobalConfig.builder().
                        applicationConfig(ctx.getBean(ApplicationConfig.class)).
                        registryConfig(ctx.getBean(RegistryConfig.class)).
                        protocolConfig(ctx.getBean(ProtocolConfig.class)).
                        clusterConfig(ctx.getBean(ClusterConfig.class)).
                        build());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException{
        return bean;
    }
}
