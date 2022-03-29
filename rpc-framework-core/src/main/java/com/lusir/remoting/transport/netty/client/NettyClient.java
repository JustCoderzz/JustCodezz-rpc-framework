package com.lusir.remoting.transport.netty.client;

import com.lusir.factory.SingletonFactory;
import com.lusir.remoting.dto.RpcRequest;
import com.lusir.remoting.dto.RpcResponse;
import com.lusir.remoting.transport.RpcRequestTransport;
import com.lusir.remoting.transport.netty.codec.RpcMessageDecoder;
import com.lusir.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author lusir
 * @date 2022/3/29 - 13:05
 **/
@Slf4j
public class NettyClient implements RpcRequestTransport {
    private final ChannelProvider channelProvider;
    private  final Bootstrap bootstrap;
    private  final EventLoopGroup worker;
    private final  UnprocessedRequests unprocessedRequests;


    public  NettyClient() {

        worker=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
        bootstrap.group(worker)
                .channel(SocketChannel.class)
                .handler(new LoggingHandler())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        p.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new NettyClientHandler());
                    }
                });

        unprocessedRequests=SingletonFactory.getInstance(UnprocessedRequests.class);
        channelProvider =SingletonFactory.getInstance(ChannelProvider.class);

    }
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture=new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                 if (channelFuture.isSuccess()) {
                     log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                     completableFuture.complete(channelFuture.channel());
                 }else {
                     throw new IllegalStateException();
                 }
            }
        });
        return completableFuture.get();

    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> future=new CompletableFuture<>();
    }


    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel==null) {
            channel= doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress,channel);
        }
        return channel;
    }

    public void close() {
        worker.shutdownGracefully();
    }
}
