package com.luzyi.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author lusir
 * @date 2022/3/25 - 13:29
 **/
@Slf4j
public class EventLoopGroupTest {
    public static void main(String[] args) {
        EventLoopGroup group=new NioEventLoopGroup(2);//若没有指定则使用默认值
//        System.out.println(NettyRuntime.availableProcessors());
//        System.out.println(group.next());
//        System.out.println(group.next());
//        System.out.println(group.next());
//        事件轮询
//io.netty.channel.nio.NioEventLoop@6c629d6e
//io.netty.channel.nio.NioEventLoop@5ecddf8f
//io.netty.channel.nio.NioEventLoop@6c629d6e

//        执行普通任务
        group.next().submit(()->{
            log.info("OK");
        });

//        执行定时任务
        group.next().scheduleAtFixedRate(()->{
            log.info("ok");
        },0,1, TimeUnit.SECONDS);

    }
}
