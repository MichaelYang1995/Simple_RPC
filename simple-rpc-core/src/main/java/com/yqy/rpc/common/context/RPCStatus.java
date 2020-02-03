package com.yqy.rpc.common.context;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: RPCStatus
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/11/29 20:27
 * @Version: 1.0
 */
@Log4j
public class RPCStatus {
    /**
     * RPC请求活跃度映射表
     * k:由服务接口名+方法名+服务器IP地址生产的key
     * v:活跃度
     */
    private static final Map<String, Integer> RPC_ACTIVE_COUNT = new ConcurrentHashMap<>();

    public synchronized static int getActivity(String interfaceName, String methodName, String address){
        Integer count = RPC_ACTIVE_COUNT.get(generateKey(interfaceName, methodName, address));
        return count==null?0:count;

    }

    public synchronized static void inActivity(String interfaceName, String methodName, String address){
        String key = generateKey(interfaceName, methodName, address);
        if (RPC_ACTIVE_COUNT.containsKey(key)){
            RPC_ACTIVE_COUNT.put(key, RPC_ACTIVE_COUNT.get(key)+1);
        }else {
            RPC_ACTIVE_COUNT.put(key, 1);
        }
    }

    public synchronized static void deActivity(String interfaceName, String methodName, String address){
        String key = generateKey(interfaceName, methodName, address);
        if (RPC_ACTIVE_COUNT.containsKey(key)){
            RPC_ACTIVE_COUNT.put(key, RPC_ACTIVE_COUNT.get(key)-1);
        }
    }

    private static String generateKey(String interfaceName, String methodName, String address){
        return interfaceName + "." + methodName + "." + address;
    }
}
