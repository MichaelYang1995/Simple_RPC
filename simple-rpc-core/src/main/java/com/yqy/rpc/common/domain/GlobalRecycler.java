package com.yqy.rpc.common.domain;

import java.util.HashMap;
import java.util.Map;

import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import io.netty.util.Recycler;
/**
 * @ClassName: GlobalRecycler
 * @Description: 基于Netty的全局对象池(Recycler)
 *               实现对象的复用,避免频繁地创建对象及GC
 * @Author: YangQingyuan
 * @Date: 2019/11/30 13:05
 * @Version: 1.0
 */
public class GlobalRecycler {
    /**
     * 存储对象类型及其对象池
     * k: 循环对象类型
     * v: 循环对象
     */
    private static Map<Class<?>, Recycler<?>> RECYCLER = new HashMap<>();

    static {
        RECYCLER.put(RPCRequest.class, new Recycler<RPCRequest>() {
           @Override
           protected RPCRequest newObject(Handle<RPCRequest> handle){
               return new RPCRequest(handle);
           }
        });
        RECYCLER.put(RPCResponse.class, new Recycler<RPCResponse>() {
            @Override
            protected RPCResponse newObject(Handle<RPCResponse> handle){
                return new RPCResponse(handle);
            }
        });
    }

    public static boolean isReusable(Class<?> clazz){
        return RECYCLER.containsKey(clazz);
    }

    public static <T> T reuse(Class<T> clazz){
        if (!isReusable(clazz)){
            throw new RPCException(ExceptionEnum.RECYCLER_ERROR, "RECYCLER_ERROR");
        }
        return (T)RECYCLER.get(clazz).get();
    }
}
