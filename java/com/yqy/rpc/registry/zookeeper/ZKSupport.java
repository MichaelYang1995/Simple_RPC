package com.yqy.rpc.registry.zookeeper;

import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: ZKSupport
 * @Description: zookeeper注册中心的支持类，提供以下功能:
 *               1）连接zookeeper注册中心服务器
 *               2）创建Znode节点
 *               3）创建ZK路径
 *               4）关闭与zookeeper服务器的连接
 * @Author: YangQingyuan
 * @Data: 2019/11/16
 * @Version: V1.0
 **/
@Slf4j
public class ZKSupport {
    protected ZooKeeper zooKeeper = null;

    private volatile CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final int ZK_SESSION_TIME = 5000;

    public void connect(String address) {
        try{
            this.zooKeeper = new ZooKeeper(address, ZK_SESSION_TIME, (WatchedEvent watchedEvent) -> {
               Watcher.Event.KeeperState keeperState = watchedEvent.getState();
               Watcher.Event.EventType eventType = watchedEvent.getType();
               //如果是建立连接
               if (keeperState == Watcher.Event.KeeperState.SyncConnected){
                   //连接建立成功
                   if (eventType == Watcher.Event.EventType.None){
                       countDownLatch.countDown();
                       log.info("ZK建立连接");
                   }
               }
            });
            log.info("开始连接ZK服务器");
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createPathIfAbsent(String path, CreateMode createMode) throws KeeperException, InterruptedException {
        String[] split = path.split("/");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<split.length;i++){
            if (StringUtils.hasText(split[i])){
                Stat stat = zooKeeper.exists(stringBuilder.toString(), false);
                if (stat == null){
                    zooKeeper.create(stringBuilder.toString(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                }
            }
            if (i < split.length - 1){
                stringBuilder.append("/");
            }
        }
    }

    public void createNodeIfAbsent(String data, String path) {
        try{
            byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
            //建立临时节点
            zooKeeper.create(path + "/" + data, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }catch (KeeperException e){
            if (e instanceof KeeperException.NodeExistsException){
                throw new RPCException(ExceptionEnum.REGISTRY_ERROR, "ZK路径"+path+"下已经存在节点"+data);
            }else {
                e.printStackTrace();
            }
        }catch (InterruptedException e){
            e.getCause();
        }
    }

    public void close() throws InterruptedException {
        if (zooKeeper != null){
            zooKeeper.close();
        }
    }

    public List<String> getChildren(String address, Watcher watcher) throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(address, watcher);
    }

    public byte[] getData(String address, Watcher watcher) throws KeeperException, InterruptedException {
        return zooKeeper.getData(address, watcher, null);
    }
}
