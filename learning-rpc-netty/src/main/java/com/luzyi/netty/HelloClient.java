package com.luzyi.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author lusir
 * @date 2022/3/25 - 12:39
 **/
@Slf4j
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group= new NioEventLoopGroup();
        ChannelFuture future= new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost",8080));

//            future.sync();   方案1
//            Channel channel= future.channel();
//            log.info("{}",channel);
//            channel.writeAndFlush("hello world");

//        方案2
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel= future.channel();
                log.debug("{}",channel);
                channel.writeAndFlush("hello world");
//                线程优雅的关闭  也就是拒绝接受新的线程  等线程任务执行完毕才关闭
                group.shutdownGracefully();
            }
        });

    }
}
