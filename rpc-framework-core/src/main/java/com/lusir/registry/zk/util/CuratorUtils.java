package com.lusir.registry.zk.util;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author lusir
 * @date 2022/3/29 - 19:02
 **/
@Slf4j
public class CuratorUtils {

    public static final String ZK_REGISTER_ROOT_PATH = "/lusir-rpc";
    private static final Set<String> REGISTERED_PATH_SET= ConcurrentHashMap.newKeySet();
    private static  final Map<String,List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public CuratorUtils() {

    }


    public static void createPersistentNode(CuratorFramework zkClient,String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path)||zkClient.checkExists().forPath(path)!=null) {
                log.info("The node already exists. The node is:[{}]", path);
            }else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        }catch (Exception e){
            log.error("create persistent node for path [{}] fail", path);
        }

    }

    public static CuratorFramework getZkClient() {
        String zkAddress=DEFAULT_ZOOKEEPER_ADDRESS;
        if (zkClient!=null&&zkClient.getState()== CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(BASE_SLEEP_TIME,MAX_RETRIES);
        zkClient=CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;

    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient,String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)){
            return  SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> serviceList=null;
        String servicePath=ZK_REGISTER_ROOT_PATH+"/"+rpcServiceName;
        try {
             serviceList = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName,serviceList);
            registerWatcher(rpcServiceName,zkClient);
        }catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return serviceList;
    }

    public static void registerWatcher(String rpcServiceName,CuratorFramework zkClient) throws Exception {
        String root=ZK_REGISTER_ROOT_PATH+"/"+rpcServiceName;
        PathChildrenCache pathChildrenCache=new PathChildrenCache(zkClient,root,true);
        PathChildrenCacheListener pathChildrenCacheListener=new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                List<String> serviceList = curatorFramework.getChildren().forPath(root);
                SERVICE_ADDRESS_MAP.put(rpcServiceName,serviceList);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();

    }
}
