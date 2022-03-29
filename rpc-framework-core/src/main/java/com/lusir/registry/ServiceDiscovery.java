package com.lusir.registry;

import com.lusir.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author lusir
 * @date 2022/3/29 - 18:56
 **/
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
