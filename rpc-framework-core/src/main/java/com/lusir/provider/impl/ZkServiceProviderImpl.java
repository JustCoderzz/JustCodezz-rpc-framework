package com.lusir.provider.impl;

import com.lusir.config.RpcServiceConfig;
import com.lusir.enums.RpcErrorMessageEnum;
import com.lusir.exception.RpcException;
import com.lusir.extension.ExtensionLoader;
import com.lusir.provider.ServiceProvider;
import com.lusir.registry.ServiceRegistry;
import com.lusir.registry.zk.ZkServiceDiscoveryImpl;
import com.lusir.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lusir
 * @date 2022/3/30 - 14:51
 **/
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {
    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProviderImpl(){
        serviceMap=new ConcurrentHashMap<>();
        registeredService=ConcurrentHashMap.newKeySet();
        serviceRegistry= ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }
    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName=rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) return;
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName,rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
            try {
                String host= InetAddress.getLocalHost().getHostAddress();
                this.addService(rpcServiceConfig);
                serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(),new InetSocketAddress(host, NettyRpcServer.port));
            }catch (Exception e){
                log.error("occur exception when getHostAddress", e);
            }
    }
}
