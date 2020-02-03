package com.yqy.rpc.common.enumeration.support;

import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.exception.RPCException;

/**
 * @ClassName: ExtensionBaseType
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/1 15:55
 * @Version: 1.0
 */
public interface ExtensionBaseType<T> {
    T getInstance();

    static ExtensionBaseType valueOf(Class enumType, String s){
        Enum wantedEnum = Enum.valueOf(enumType, s);
        if (wantedEnum instanceof ExtensionBaseType){
            return (ExtensionBaseType) wantedEnum;
        }else {
            throw new RPCException(ExceptionEnum.VALUE_OF_MUST_BE_APPLIED_TO_EXTENSION_ENUM_TYPE, "VALUE_OF_MUST_BE_APPLIED_TO_EXTENSION_ENUM_TYPE");
        }
    }
}
