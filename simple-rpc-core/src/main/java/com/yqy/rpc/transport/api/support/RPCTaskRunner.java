package com.yqy.rpc.transport.api.support;

import com.yqy.rpc.common.domain.GlobalRecycler;
import com.yqy.rpc.common.domain.Message;
import com.yqy.rpc.common.domain.RPCRequest;
import com.yqy.rpc.common.domain.RPCResponse;
import com.yqy.rpc.config.ServiceConfig;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName: RPCTaskRunner
 * @Description: 执行RPC任务的任务执行器
 *               根据ServiceConfig信息调用的真正的实现类
 * @Author: YangQingyuan
 * @Data: 2019/11/22
 * @Version: V1.0
 **/
@Slf4j
public class RPCTaskRunner implements Runnable{
    private RPCRequest request;

    private ServiceConfig serviceConfig;

    //RPC请求所在Channel的HandlerContext对象,用于将RPCResponse刷出站
    private ChannelHandlerContext ctx;

    public RPCTaskRunner(ChannelHandlerContext ctx, RPCRequest request, ServiceConfig serviceConfig){
        this.ctx = ctx;
        this.request = request;
        this.serviceConfig = serviceConfig;
    }

    @Override
    public void run() {
        //调用请求有两种情况:
        //1)正常的RPC请求
        //2)RPC回调请求
        if(serviceConfig.isCallback()){
            //如果该请求是从客户端传入的回调请求,则需要以代理的方式调用,因为要生成RPCRequest对象
            try{
                handle(request);
                request.recyle();
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
            return;
        }

        //从对象池中取RPCResponse对象进行复用
        RPCResponse response = GlobalRecycler.reuse(RPCResponse.class);
        response.setRequestID(request.getRequestID());
        try {
            Object result = handle(request);
            response.setResult(result);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            response.setErrorCause(throwable);
        }
        log.info("本地调用执行完成, 执行结果:{}", response);
        //如果调用方式为callback,且该请求为服务端的回调请求则不需要响应
        if (!serviceConfig.isCallbackInterface()){
            ctx.writeAndFlush(Message.bulidResponse(response));
        }
    }

    private Object handle(RPCRequest request) throws Throwable{
        Object serviceBean = serviceConfig.getRef();

        String methodName = request.getMethodName();
        Class<?> interfaceClass = serviceBean.getClass();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        int callbackParameterIndex = serviceConfig.getCallbackParameterIndex();
        Method method = interfaceClass.getMethod(request.getMethodName(), parameterTypes[callbackParameterIndex]);
        method.setAccessible(true);

        if (serviceConfig.isCallback()){
            parameters[callbackParameterIndex] = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals(serviceConfig.getCallbackMethod())){
                                RPCRequest rpcRequest = GlobalRecycler.reuse(RPCRequest.class);
                                rpcRequest.setRequestID(request.getRequestID());
                                rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
                                rpcRequest.setMethodName(method.getName());
                                rpcRequest.setParameterTypes(method.getParameterTypes());
                                rpcRequest.setParameters(args);
                                ctx.writeAndFlush(rpcRequest);
                                return null;
                            }else {
                                return method.invoke(proxy, args);
                            }
                        }
                    });
        }

        return method.invoke(serviceBean, parameters);
    }
}
