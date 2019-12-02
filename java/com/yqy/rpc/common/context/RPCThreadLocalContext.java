package com.yqy.rpc.common.context;

import com.yqy.rpc.protocol.api.Invoker;

import java.util.concurrent.Future;

/**
 * @ClassName: RPCThreadLocalContext
 * @Description: 一个线程的私有类,存储一些线程私有的对象(存储一些线程私有的对象)
 *               1）protocol层面的invoker
 *               2）每个invoker对应的Future对象
 * @Author: YangQingyuan
 * @Date: 2019/11/29 21:50
 * @Version: 1.0
 */
public class RPCThreadLocalContext {
    private static final ThreadLocal<RPCThreadLocalContext> RPC_CONTEXT = new ThreadLocal<>(){
        @Override
        protected RPCThreadLocalContext initialValue(){
            return new RPCThreadLocalContext();
        }
    };

    //从threadLocal中取出当前线程对应的RPCThreadLocalContext对象
    public static RPCThreadLocalContext getContext(){
        return RPC_CONTEXT.get();
    }

    private Invoker invoker;

    private Future future;

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }
}
