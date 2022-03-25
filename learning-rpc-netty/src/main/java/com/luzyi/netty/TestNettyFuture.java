package com.luzyi.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Callable;

/**
 * @author lusir
 * @date 2022/3/25 - 15:11
 **/
public class TestNettyFuture {
    public static void main(String[] args) {

        EventLoopGroup group=new NioEventLoopGroup();
        final Future<Integer> res = group.next().submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 10;
            }
        });
//        res.getNow();
        res.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                future.getNow();
            }
        });
    }
}
