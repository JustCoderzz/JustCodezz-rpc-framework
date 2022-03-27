package com.luzyi.server.handler;

import com.luzyi.dto.RpcRequest;
import com.luzyi.dto.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lusir
 * @date 2022/3/27 - 9:23
 **/
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private  static final AtomicInteger ATOMIC_INTEGER=new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcRequest rpcRequest=(RpcRequest)  msg;
            log.info("server receive msg:{},times,{}",rpcRequest,ATOMIC_INTEGER.getAndIncrement());
            RpcResponse rpcResponse=RpcResponse.builder().message("message from server").build();
            ChannelFuture future = ctx.writeAndFlush(rpcResponse);
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception",cause);
        ctx.close();
    }
}
