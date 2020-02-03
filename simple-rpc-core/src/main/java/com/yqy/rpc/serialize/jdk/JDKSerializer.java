package com.yqy.rpc.serialize.jdk;

import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.serialize.api.Serializer;

import java.io.*;

/**
 * @ClassName: JDKSerializer
 * @Description: 基于JDK自带的序列化/反序列算法
 * @Author: YangQingyuan
 * @Data: 2019/11/26
 * @Version: V1.0
 **/
public class JDKSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T o) throws RPCException {
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            byte[] bytes = baos.toByteArray();
            baos.close();
            oos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPCException(ExceptionEnum.SERIALIZE_ERROR, "SERIALIZE_ERROR");
        }
    }

    @Override
    public <T> T deSerializer(byte[] bytes, Class<T> clazz) throws RPCException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object res = ois.readObject();
            return clazz.cast(res); //强制类型转换
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RPCException(ExceptionEnum.DESERIALIZE_ERROR, "DESERIALIZE_ERROR");
        }
    }
}
