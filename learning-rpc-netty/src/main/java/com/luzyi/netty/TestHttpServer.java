package com.luzyi.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LoggingHandler;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * @author lusir
 * @date 2022/3/25 - 19:30
 **/
public class TestHttpServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup worker=new NioEventLoopGroup();
        try {
             ServerBootstrap bootstrap = new ServerBootstrap();
             bootstrap.group(boss,worker);
             bootstrap.channel(NioServerSocketChannel.class);
             bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel channel) throws Exception {
                     channel.pipeline().addLast(new LoggingHandler());
                     channel.pipeline().addLast(new HttpServerCodec());
                     channel.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {

                         @Override
                         protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {

                             System.out.println(msg.uri());

                             DefaultFullHttpResponse response=new DefaultFullHttpResponse(msg.protocolVersion(),HttpResponseStatus.OK);
                              byte[] bytes = "<h1>hello world<h1>".getBytes();

                              response.headers().setInt(CONTENT_LENGTH,bytes.length);
                              response.content().writeBytes(bytes);

                              ctx.writeAndFlush(response);
                         }
                     });
                 }
             }).bind(8080)
                     .sync()
                     .channel()
                     .closeFuture()
                     .sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
