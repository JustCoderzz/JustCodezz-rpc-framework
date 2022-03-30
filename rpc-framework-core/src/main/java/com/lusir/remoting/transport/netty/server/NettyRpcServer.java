package com.lusir.remoting.transport.netty.server;

import com.lusir.config.RpcServiceConfig;
import com.lusir.factory.SingletonFactory;
import com.lusir.provider.ServiceProvider;
import com.lusir.remoting.transport.netty.codec.RpcMessageDecoder;
import com.lusir.remoting.transport.netty.codec.RpcMessageEncoder;
import com.lusir.utils.RuntimeUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author lusir
 * @date 2022/3/29 - 18:45
 **/
@Slf4j
public class NettyRpcServer {
    public   final static  int port=8888;

    private final ServiceProvider serviceProvider= SingletonFactory.getInstance(ServiceProvider.class);

    public  void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start() {
        String host= InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup=new DefaultEventExecutorGroup(RuntimeUtils.getCPUs()*2);

        try {
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .handler(new LoggingHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline p = channel.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup,new NettyRpcServiceHandler());
                        }
                    });
            ChannelFuture ch = bootstrap.bind(host, port).sync();
            ch.channel().closeFuture().sync();
        }catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
