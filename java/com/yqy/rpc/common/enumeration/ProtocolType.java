package com.yqy.rpc.common.enumeration;

import com.yqy.rpc.common.enumeration.support.ExtensionBaseType;
import com.yqy.rpc.protocol.api.Protocol;
import com.yqy.rpc.protocol.wind.WindProtocol;

/**
 * @ClassName: ProtocolType
 * @Description: TODO
 * @Author: YangQingyuan
 * @Date: 2019/12/1 20:59
 * @Version: 1.0
 */
public enum ProtocolType implements ExtensionBaseType<Protocol> {
    WIND(new WindProtocol());     //Wind协议

    private Protocol protocol;

    ProtocolType(Protocol protocol){
        this.protocol = protocol;
    }

    @Override
    public Protocol getInstance() {
        return protocol;
    }
}
