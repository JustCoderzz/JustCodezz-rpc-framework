package com.lusir.registry.zk;

import com.lusir.registry.ServiceDiscovery;
import com.lusir.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author lusir
 * @date 2022/3/29 - 19:01
 **/
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        return null;
    }
}
