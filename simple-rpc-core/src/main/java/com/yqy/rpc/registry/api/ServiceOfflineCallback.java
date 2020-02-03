package com.yqy.rpc.registry.api;

import java.util.List;

/**
 * @ClassName: ServiceOfflineCallback
 * @Description: 服务下线回调接口
 *               ClusterInvoker调用discovery()接口的时候，将此接口的实现类作为参数传入
 *               服务器有服务下线的时候，注册中心回调该接口的实现方法，删除对应的ServiceURL
 * @Author: YangQingyuan
 * @Data: 2019/11/16
 * @Version: V1.0
 **/
public interface ServiceOfflineCallback {
    void removeNotExisted(List<ServiceURL> newServiceURLs);
}
