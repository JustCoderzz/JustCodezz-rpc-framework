package com.lusir.remoting.handler;

import com.lusir.exception.RpcException;
import com.lusir.factory.SingletonFactory;
import com.lusir.provider.ServiceProvider;
import com.lusir.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lusir
 * @date 2022/3/30 - 16:01
 **/
@Slf4j
public class RpcRequestHandler {

    public final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider= SingletonFactory.getInstance(ServiceProvider.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest,service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result= method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
