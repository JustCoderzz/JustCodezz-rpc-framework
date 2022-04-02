package com.lusir.loadbalance.loadbalacer;

import com.lusir.loadbalance.AbstractLoadBalance;
import com.lusir.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @author lusir
 * @date 2022/4/2 - 14:16
 **/
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest) {
        Random random=new Random();
        return serviceUrlList.get(random.nextInt(serviceUrlList.size()));
    }
}
