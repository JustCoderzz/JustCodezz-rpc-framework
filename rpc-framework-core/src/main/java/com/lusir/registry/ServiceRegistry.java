package com.lusir.registry;

import java.net.InetSocketAddress;

/**
 * @author lusir
 * @date 2022/3/29 - 18:56
 **/
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
