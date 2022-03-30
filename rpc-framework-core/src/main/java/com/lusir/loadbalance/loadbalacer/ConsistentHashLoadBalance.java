package com.lusir.loadbalance.loadbalacer;

import com.lusir.loadbalance.AbstractLoadBalance;
import com.lusir.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lusir
 * @date 2022/3/29 - 20:20
 **/
@Slf4j
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();
    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest) {
        int identityHashCode=System.identityHashCode(serviceUrlList);

        String rpcServiceName=rpcRequest.getRpcServiceName();
        ConsistentHashSelector consistentHashSelector = selectors.get(rpcServiceName);

        if (consistentHashSelector==null||consistentHashSelector.identityHashCode!=identityHashCode) {
            selectors.put(rpcServiceName,new ConsistentHashSelector(serviceUrlList, 160,identityHashCode));
           consistentHashSelector= selectors.get(rpcServiceName);
        }
      return   consistentHashSelector.select(rpcServiceName+ Arrays.stream(rpcRequest.getParameters()));
    }

    static class ConsistentHashSelector {
        private final TreeMap<Long,String> virtualInvokers;

        private final int identityHashCode;

        ConsistentHashSelector(List<String> invokers,int replicaNumber,int identityHashCode) {
            this.virtualInvokers=new TreeMap<>();
            this.identityHashCode=identityHashCode;

            for (String invoker:invokers) {
                for (int i=0;i<replicaNumber/4;i++) {
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md=MessageDigest.getInstance("md5");
                md.update(key.getBytes(StandardCharsets.UTF_8));
            }catch (Exception e){
                throw new IllegalArgumentException(e);
            }
            return md.digest();
        }
        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }
        public String selectForKey(Long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry==null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }
}
