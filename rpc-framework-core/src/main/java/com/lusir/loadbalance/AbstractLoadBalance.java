package com.lusir.loadbalance;

import com.lusir.remoting.dto.RpcRequest;
import com.lusir.utils.CollectionUtils;

import java.util.List;

/**
 * @author lusir
 * @date 2022/3/29 - 20:15
 **/
public abstract class AbstractLoadBalance implements LoadBalance{
    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            return null;
        }
        if (serviceUrlList.size()==1) {
            return serviceUrlList.get(0);
        }
         return  doSelect(serviceUrlList,rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest);
}
