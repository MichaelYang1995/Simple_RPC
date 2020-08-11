# Simple_RPC
simple-rpc是一个基于netty+springboot+zookeeper的分布式服务框架，具有服务注册与发现、远程服务调用和集群负载均衡等基本功能。架构上参考了dubbo的分层设计，各功能模块放在不同的包中，通过API相互关联实现解耦。
## 功能列表
*  基于Netty实现客户端与服务端之间的通信，包括空闲检测、心跳保持、解决粘包半包等问题
*  基于zookeeper实现服务的注册与发现，利用zookeeper的监听机制实现服务的订阅/推送
*  实现了一致性哈希/轮询/随机/加权随机和最小活跃度等负载均衡算法
*  实现了failover/failfast/failsafe三种集群容错机制
*  基于springboot的自动配置功能实现bean的加载，并且自定义了一个springboot-starter插件供用户使用
*  实现简易扩展点(类似Java的SPI机制)
## 分层设计
分层设计是为了解耦，下层向上层暴露接口，上层只需要调用下层的接口而无需关心实现，具体实现类由用户通过配置文件来配置，避免硬编码。
## 架构图
### 初始化
![image](https://github.com/MichaelYang1995/Simple_RPC/blob/master/img/%E5%88%9D%E5%A7%8B%E5%8C%96.jpg)
### 服务调用之ConsumerSide
![image](https://github.com/MichaelYang1995/Simple_RPC/blob/master/img/%E6%9C%8D%E5%8A%A1%E8%B0%83%E7%94%A8%E4%B9%8BConsumerSide.jpg)
### 服务调用之ProviderSide
![image](https://github.com/MichaelYang1995/Simple_RPC/blob/master/img/%E6%9C%8D%E5%8A%A1%E8%B0%83%E7%94%A8%E4%B9%8BProviderSide.jpg)
