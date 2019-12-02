package com.yqy.rpc.common.enumeration;

import com.yqy.rpc.common.enumeration.support.ExtensionBaseType;
import com.yqy.rpc.serialize.api.Serializer;
import com.yqy.rpc.serialize.hessian.HessianSerializer;
import com.yqy.rpc.serialize.jdk.JDKSerializer;
import com.yqy.rpc.serialize.json.JsonSerializer;
import com.yqy.rpc.serialize.protostuff.ProtostuffSerializer;

/**
 * @ClassName: SerializerType
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/2 10:29
 * @Version: 1.0
 */
public enum SerializerType implements ExtensionBaseType<Serializer> {
    JDK(new JDKSerializer()),                     //jdk原生序列化
    HESSIAN(new HessianSerializer()),             //hessian序列化
    PROTOSTUFF(new ProtostuffSerializer()),       //protoStuff序列化
    JSON(new JsonSerializer());                   //Json序列化

    private Serializer serializer;

    SerializerType(Serializer serializer){
        this.serializer = serializer;
    }

    @Override
    public Serializer getInstance() {
        return serializer;
    }
}
