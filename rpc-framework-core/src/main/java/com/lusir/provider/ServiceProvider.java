package com.lusir.provider;

import com.lusir.config.RpcServiceConfig;

/**
 * @author lusir
 * @date 2022/3/30 - 14:47
 **/
public interface ServiceProvider {

    void addService(RpcServiceConfig rpcServiceConfig);

    Object getService(String rpcServiceName);

    void publishService(RpcServiceConfig rpcServiceConfig);
}
