package com.yqy.rpc.protocol.api.support;

import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.protocol.api.InvokeParam;
import com.yqy.rpc.protocol.api.Invoker;

/**
 * @ClassName: DelegateInvoker
 * @Description: 封装了Filter链的Invoker对象
 *               提供一个抽象的
 * @Author: YangQingyuan
 * @Data: 2019/11/20
 * @Version: V1.0
 **/
public abstract class DelegateInvoker<T> extends AbstractInvoker<T> {
    private Invoker<T> delegate;

    public DelegateInvoker(Invoker<T> invoker) {
        this.delegate = invoker;
    }

    public Invoker<T> getDelegate() {
        return delegate;
    }

    public abstract RPCResponse invoke(InvokeParam invokeParam);
}
