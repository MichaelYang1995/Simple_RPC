package com.yqy.rpc.common.domain;

import com.yqy.rpc.common.util.TypeUtil;
import io.netty.util.Recycler;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @ClassName: RPCRequest
 * @Description: RPC调用请求对象
 * @Author: YangQingyuan
 * @Date: 2019/11/30 13:52
 * @Version: 1.0
 */
@Data
public class RPCRequest implements Serializable {
    private String requestID;

    private String interfaceName;

    private String methodName;

    private String[] parameterTypes;

    private Object[] parameters;

    /**
     * handle为Recyler对象池中封装RPCRequest的对象,不可序列化
     * 任何使用Recyler对象池复用技术的对象都会被封装成DefaultHandle对象(实现了Handle接口)
     * Handle接口只有一个方法:recycle(),用于回收对象至对象池
     */
    private final transient Recycler.Handle<RPCRequest> handle;

    public RPCRequest(Recycler.Handle<RPCRequest> handle){
        this.handle = handle;
    }

    public Class[] getParameterTypes(){
        Class[] parameterTypeClasses = new Class[parameterTypes.length];
        for (int i=0;i<parameterTypes.length;i++){
            String type = parameterTypes[i];
            try{
                if (TypeUtil.isPrimitive(type)){
                    //基本类型
                    parameterTypeClasses[i] = TypeUtil.map(type);
                }else {
                    //非基本类型
                    parameterTypeClasses[i] = Class.forName(type);
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        return parameterTypeClasses;
    }

    public void setParameterTypes(String[] parameterTypes){
        this.parameterTypes = parameterTypes;
    }

    public void setParametersTypes(Class[] parametersTypes){
        String[] stringparametersTypes = new String[parametersTypes.length];
        for (int i = 0; i<parametersTypes.length; i++){
            stringparametersTypes[i] = parametersTypes[i].getName();
        }
        this.parameterTypes = stringparametersTypes;
    }

    public String key(){
        return interfaceName+"."+
                methodName+"."+
                Arrays.toString(parameterTypes)+"."+
                Arrays.toString(parameters);
    }

    //回收对象至对象池
    public void recyle(){
        requestID = null;
        interfaceName = null;
        methodName = null;
        parameterTypes   = null;
        parameters = null;
        handle.recycle(this);
    }
}
