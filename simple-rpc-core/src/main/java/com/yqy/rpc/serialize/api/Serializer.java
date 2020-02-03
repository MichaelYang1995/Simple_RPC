package com.yqy.rpc.serialize.api;

import com.yqy.rpc.common.exception.RPCException;

/**
 * @ClassName: Serializer
 * @Description: 序列化/反序列化接口
 * @Author: YangQingyuan
 * @Data: 2019/11/26
 * @Version: V1.0
 **/
public interface Serializer {
    /**
     * 序列化
     * @param o     待序列化对象
     * @param <T>   待序列化对象类型
     * @return      序列化字节数组
     */
    <T> byte[] serialize(T o) throws RPCException;

    /**
     * 反序列化
     * @param bytes   字节数组
     * @param clazz   目标对象类型
     * @param <T>     目标对象类型
     * @return        目标对象
     */
    <T> T deSerializer(byte[] bytes, Class<T> clazz) throws RPCException;
}
