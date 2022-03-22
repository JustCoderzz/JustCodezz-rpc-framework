package com.luzyi.client;

import io.netty.bootstrap.Bootstrap;
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
        b=new Bootstrap();
    }
}
