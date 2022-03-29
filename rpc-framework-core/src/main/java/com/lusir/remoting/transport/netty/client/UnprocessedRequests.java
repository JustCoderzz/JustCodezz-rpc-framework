package com.lusir.remoting.transport.netty.client;

import com.lusir.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lusir
 * @date 2022/3/29 - 18:23
 **/
public class UnprocessedRequests {
    private static  final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES=new ConcurrentHashMap<>();

    public void put(String requestId,CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId,future);
    }

    public  void complete(RpcResponse<Object> rpcResponse ) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
