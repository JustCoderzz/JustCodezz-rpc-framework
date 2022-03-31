package com.lusir.Server;

import com.lusir.HelloService.HelloServiceImpl;
import com.lusir.Test.HelloService;
import com.lusir.annotation.RpcScan;
import com.lusir.config.RpcServiceConfig;
import com.lusir.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author lusir
 * @date 2022/3/31 - 19:14
 **/
@RpcScan(basePackage = "com.lusir")
public class NettyServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext=new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        HelloService helloService2 = new HelloServiceImpl();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2").version("version2").service(helloService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
