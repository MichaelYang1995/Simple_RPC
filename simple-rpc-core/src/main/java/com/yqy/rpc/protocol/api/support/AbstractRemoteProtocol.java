package com.yqy.rpc.protocol.api.support;

import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.registry.api.ServiceURL;
import com.yqy.rpc.transport.api.Client;
import com.yqy.rpc.transport.api.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: AbstractRemoteProtocol
 * @Description: 抽象的远程协议对象
 *               <>
 *                   负责具体远程协议的辅助工作
 *                   从这一层开始涉及到底层运输层的东西,之所以在这里管理client连接,是因为connection是address维度的
 *                   即两个address之间只需要一条连接,与它们之间有多少调用关系无关
 *                   一条连接上的endPoints中
 *               </>
 * @Author: YangQingyuan
 * @Data: 2019/11/20
 * @Version: V1.0
 **/
@Slf4j
public abstract class AbstractRemoteProtocol extends AbstractProtocol{
    /**
     * key:address
     * value:Client
     * 一个address对应一个client连接
     * 一条连接上的两个endPoint不论invoker是否相同,都共享连接,避免毫无意义地重复创建
     */
    private Map<String, Client> clientMap = new ConcurrentHashMap<>();

    /**
     * 锁Map
     * 以address创建锁对象
     * 同一时刻，两个目标address相同的Protocol只能有一个获取到该address的锁对象并进入临界区
     */
    private Map<String, Object> locks = new ConcurrentHashMap<>();

    private Server server;

    protected final Client initClient(ServiceURL serviceURL){
        //目标服务器address
        String address = serviceURL.getServiceAddress();
        locks.putIfAbsent(address, new Object());
        synchronized (locks.get(address)){
            if (clientMap.containsKey(address)){
                return clientMap.get(address);
            }
            Client client = doInitClient(serviceURL);
            clientMap.put(address, client);
            return client;
        }
    }

    protected abstract Client doInitClient(ServiceURL serviceURL);

    /**
     * 更新端点配置
     * @param serviceURL
     */
    public final void updateEndpointConfig(ServiceURL serviceURL){
        if (!clientMap.containsKey(serviceURL.getServiceAddress())){
            throw new RPCException(ExceptionEnum.SERVER_ADDRESS_IS_NOT_CONFIGURATION, "SERVER_ADDRESS_IS_NOT_CONFIGURATION");
        }

        clientMap.get(serviceURL.getServiceAddress()).updateServiceConfig(serviceURL);
    }

    public final void closeEndpoint(String address){
        Client client = clientMap.remove(address);

        if (client!=null){
            log.info("正在关闭客户端..."+address);
            client.close();
        }else{
            log.error("请不要重复关闭客户端");
        }
    }

    protected synchronized final void openServer(){
        if (server == null){
            server = doOpenServer();
        }
    }

    protected abstract Server doOpenServer();

    @Override
    public void close() throws InterruptedException {
        clientMap.values().forEach(Client::close);
        if (server != null){
            server.close();
        }
    }
}
