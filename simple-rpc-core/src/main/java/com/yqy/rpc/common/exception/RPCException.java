package com.yqy.rpc.common.exception;

import com.yqy.rpc.common.enumeration.ExceptionEnum;

/**
 * @ClassName: RPCException
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/11/30 15:09
 * @Version: 1.0
 */
public class RPCException extends RuntimeException{

    /**
     * 异常枚举类实例
     */
    private ExceptionEnum exceptionEnum;

    public RPCException(ExceptionEnum exceptionEnum, String message){
        super(message);
        this.exceptionEnum = exceptionEnum;
    }

    public RPCException(Throwable throwable, ExceptionEnum exceptionEnum, String message){
        super(message, throwable);
        this.exceptionEnum = exceptionEnum;
    }
}
