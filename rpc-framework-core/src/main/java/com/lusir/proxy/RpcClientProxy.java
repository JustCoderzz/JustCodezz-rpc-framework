package com.lusir.proxy;

import com.lusir.config.RpcServiceConfig;
import com.lusir.enums.RpcErrorMessageEnum;
import com.lusir.enums.RpcResponseCodeEnum;
import com.lusir.exception.RpcException;
import com.lusir.remoting.dto.RpcRequest;
import com.lusir.remoting.dto.RpcResponse;
import com.lusir.remoting.transport.RpcRequestTransport;
import com.lusir.remoting.transport.netty.client.NettyClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author lusir
 * @date 2022/3/31 - 14:42
 **/
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private  static  final String INTERFACE_NAME="interfaceName";
    private final RpcServiceConfig rpcServiceConfig;
    private final RpcRequestTransport rpcRequestTransport;
    public RpcClientProxy(RpcServiceConfig rpcServiceConfig,RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport=rpcRequestTransport;
        this.rpcServiceConfig=rpcServiceConfig;
    }
    public <T>T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest=RpcRequest.builder()
                .methodName(method.getName())
                .interfaceName(method.getDeclaringClass().getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .questId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RpcResponse response=null;
        if (rpcRequestTransport instanceof NettyClient) {
            CompletableFuture<RpcResponse<Object>> future = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            response=future.get();
        }
        check(response,rpcRequest);
        return response.getData();

    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getQuestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
