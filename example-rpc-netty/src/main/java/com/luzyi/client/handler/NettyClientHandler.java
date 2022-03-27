package com.luzyi.client.handler;

import com.luzyi.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lusir
 * @date 2022/3/26 - 22:56
 **/
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcResponse message=(RpcResponse)  msg;
            log.info("client msg:{}",message.toString());
            AttributeKey<RpcResponse> key=AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(message);
            ctx.channel().close();
        }finally {
            ReferenceCountUtil.release(msg);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client exception",cause);
        ctx.close();
    }
}
