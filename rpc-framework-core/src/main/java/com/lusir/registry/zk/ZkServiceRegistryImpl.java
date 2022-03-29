package com.lusir.registry.zk;

import com.lusir.registry.ServiceRegistry;
import com.lusir.registry.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @author lusir
 * @date 2022/3/29 - 19:00
 **/
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String path= CuratorUtils.ZK_REGISTER_ROOT_PATH+"/"+rpcServiceName+inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient,path);
    }
}
