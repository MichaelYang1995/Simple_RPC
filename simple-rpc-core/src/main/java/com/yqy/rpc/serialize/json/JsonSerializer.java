package com.yqy.rpc.serialize.json;

import com.alibaba.fastjson.JSONObject;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.serialize.api.Serializer;

/**
 * @ClassName: JsonSerializer
 * @Description: 使用alibaba的Json包, 实现基于文档的序列化:可读性好,效率较低
 * @Author: YangQingyuan
 * @Data: 2019/11/26
 * @Version: V1.0
 **/
public class JsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T o) throws RPCException {
        return JSONObject.toJSONBytes(o);
    }

    @Override
    public <T> T deSerializer(byte[] bytes, Class<T> clazz) throws RPCException {
        return JSONObject.parseObject(bytes, clazz);
    }
}
