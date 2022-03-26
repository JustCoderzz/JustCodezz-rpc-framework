package com.luzyi.client;

import com.luzyi.serialize.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lusir
 * @date 2022/3/22 - 18:11
 **/
public class NettyClient {
    private   static  final Logger LOGGER= LoggerFactory.getLogger(NettyClient.class);

    private  final String host;

    private  final String port;

    private  static  final Bootstrap b;

    public  NettyClient(String host,String port){
        this.host=host;
        this.port=port;
    }

    static {
        EventLoopGroup worker=new NioEventLoopGroup();
        b=new Bootstrap();
        KryoSerializer kryoSerializer=new KryoSerializer();
        b.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {

                    }
                })
    }
}
