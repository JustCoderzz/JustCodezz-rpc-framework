package com.lusir.loadbalance.loadbalacer;

import com.lusir.loadbalance.AbstractLoadBalance;
import com.lusir.remoting.dto.RpcRequest;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lusir
 * @date 2022/4/2 - 14:24
 **/
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private final ConcurrentHashMap<String,RoundRobinSelector> selectors=new ConcurrentHashMap<>();
    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest) {
        String rpcServiceName=rpcRequest.getRpcServiceName();
        RoundRobinSelector selecotr = selectors.get(rpcServiceName);
        if (selecotr==null) {
            selectors.put(rpcServiceName,new RoundRobinSelector(serviceUrlList));
            selecotr=selectors.get(rpcServiceName);
        }
        return selecotr.select();
    }

    static class RoundRobinSelector {
        private  int _currentIndex;
        private List<String> serviceList;

        RoundRobinSelector(List<String> serviceList) {
            this.serviceList=serviceList;

            _currentIndex=-1;
        }

        String select() {
            _currentIndex++;
            if (_currentIndex>=serviceList.size()) {
                _currentIndex=0;
            }
            return serviceList.get(_currentIndex);
        }
    }
}
