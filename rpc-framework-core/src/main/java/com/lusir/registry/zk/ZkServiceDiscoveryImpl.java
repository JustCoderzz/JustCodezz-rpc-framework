package com.lusir.registry.zk;

import com.lusir.enums.RpcErrorMessageEnum;
import com.lusir.exception.RpcException;
import com.lusir.extension.ExtensionLoader;
import com.lusir.loadbalance.LoadBalance;
import com.lusir.registry.ServiceDiscovery;
import com.lusir.registry.zk.util.CuratorUtils;
import com.lusir.remoting.dto.RpcRequest;
import com.lusir.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author lusir
 * @date 2022/3/29 - 19:01
 **/
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {


    private  final LoadBalance loadBalance;

    public   ZkServiceDiscoveryImpl() {
        this.loadBalance= ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName=rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtils.isEmpty(serviceList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
//        load balance
        String targetService=loadBalance.selectServiceAddress(serviceList,rpcRequest);
        log.info("Successfully found the service address:[{}]", targetService);
        String[] url = targetService.split(":");
        String host=url[0];
        int port=Integer.parseInt(url[1]);
        return new InetSocketAddress(host,port);
    }
}
