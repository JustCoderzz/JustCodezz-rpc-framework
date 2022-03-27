package com.luzyi.server;

import com.luzyi.codec.NettyKryoDecoder;
import com.luzyi.codec.NettyKryoEncoder;
import com.luzyi.dto.RpcRequest;
import com.luzyi.dto.RpcResponse;
import com.luzyi.serialize.KryoSerializer;
import com.luzyi.server.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lusir
 * @date 2022/3/27 - 9:24
 **/
@Slf4j
public class NettyServer {

    private final  int port;

    private NettyServer(int port) {
        this.port=port;
    }
    private  void run() {
        EventLoopGroup worker=new NioEventLoopGroup();
        EventLoopGroup boss=new NioEventLoopGroup();
        try {


        ServerBootstrap bootstrap=new ServerBootstrap();
        KryoSerializer kryoSerializer=new KryoSerializer();
        bootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.SO_BACKLOG,128)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer,RpcRequest.class));
                        channel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                        channel.pipeline().addLast(new NettyServerHandler());
                    }
                });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e) {
            log.error("err is {}",e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServer(8080).run();
    }
}
