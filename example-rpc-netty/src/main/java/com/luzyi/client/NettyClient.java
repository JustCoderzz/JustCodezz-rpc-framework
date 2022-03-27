package com.luzyi.client;

import com.luzyi.client.handler.NettyClientHandler;
import com.luzyi.codec.NettyKryoDecoder;
import com.luzyi.codec.NettyKryoEncoder;
import com.luzyi.dto.RpcRequest;
import com.luzyi.dto.RpcResponse;
import com.luzyi.serialize.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lusir
 * @date 2022/3/22 - 18:11
 **/
@Slf4j
public class NettyClient {
    private   static  final Logger LOGGER= LoggerFactory.getLogger(NettyClient.class);

    private  final String host;

    private  final int port;

    private  static  final Bootstrap b;

    public  NettyClient(String host,int port){
        this.host=host;
        this.port=port;
    }

//    初始化资源
    static {
        EventLoopGroup worker=new NioEventLoopGroup();
        b=new Bootstrap();
        KryoSerializer kryoSerializer=new KryoSerializer();
        b.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        channel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        channel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    public RpcResponse sendMessage(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = b.connect(host, port).sync();
            log.info("client connect {}",host+":"+port);
             Channel channel = future.channel();
             if (channel!=null) {
                 channel.writeAndFlush(rpcRequest).addListener(promise->{
                     if (promise.isSuccess()) {
                         log.info("client send message{}",rpcRequest.toString());
                     }else{
                         log.error("send failed",promise.cause());
                     }
                 });
                 channel.closeFuture().sync();
                 AttributeKey<RpcResponse> key=AttributeKey.valueOf("rpcResponse");
                 return channel.attr(key).get();
             }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        NettyClient client=new NettyClient("localhost",8080);
        RpcRequest rpcRequest=new RpcRequest("interfaceName","hello");
        for (int i = 0; i < 3; i++) {
          RpcResponse response=  client.sendMessage(rpcRequest);
            System.out.println(response.toString());
        }

    }

}
