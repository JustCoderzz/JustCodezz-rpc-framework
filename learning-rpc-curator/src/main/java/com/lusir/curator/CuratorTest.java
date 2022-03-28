package com.lusir.curator;

import lombok.Cleanup;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author lusir
 * @date 2022/3/28 - 18:13
 **/
public class CuratorTest {

//    CuratorFramework client;
    @Test
    public void testConnect() throws Exception{

        RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);
        CuratorFramework  client = CuratorFrameworkFactory.builder()
//                .namespace("lusir")
                .connectString("192.168.146.128:2181")
                .connectionTimeoutMs(60 * 1000)

                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(60 * 1000)
                .build();


        client.getConnectionStateListenable().addListener((clients,newState)->{
                if (newState== ConnectionState.CONNECTED) {
                    System.out.println("连接成功");
                }
        });
        client.start();
//        client.create().creatingParentsIfNeeded().withProtection().forPath("/app");

        NodeCache nodeCache=new NodeCache(client,"/app1");
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点变化了");
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(data.toString());
            }
        });
        nodeCache.start(true);
        while (true) {

        }
//        byte[] bytes = client.getData().forPath("/");
//        System.out.println(bytes.toString());

    }

//    @Test
//    public void testCreate () throws Exception{
//        client.create().forPath("/app");
//        System.out.println("1");
//    }

    @Test
    public void testFind() {

    }

//    @After
//    public void close (){
//        if (client!=null) {
//            client.close();
//        }
//    }


}
