package com.yqy.rpc.serialize.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;
import com.yqy.rpc.serialize.api.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName: HessianSerializer
 * @Description: Hessian协议序列化/反序列化算法
 * @Author: YangQingyuan
 * @Data: 2019/11/26
 * @Version: V1.0
 **/
public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T o) throws RPCException {
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HessianOutput output = new HessianOutput(baos);
            output.writeObject(o);
            output.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPCException(ExceptionEnum.SERIALIZE_ERROR, "SERIALIZE_ERROR");
        }
    }

    @Override
    public <T> T deSerializer(byte[] bytes, Class<T> clazz) throws RPCException {
        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            HessianInput input = new HessianInput(bais);
            return (T)input.readObject(clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPCException(ExceptionEnum.DESERIALIZE_ERROR, "DESERIALIZE_ERROR");
        }
    }
}
