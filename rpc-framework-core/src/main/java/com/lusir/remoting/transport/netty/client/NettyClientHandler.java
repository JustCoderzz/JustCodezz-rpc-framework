package com.lusir.remoting.transport.netty.client;

import com.lusir.enums.CompressTypeEnum;
import com.lusir.enums.SerializationTypeEnum;
import com.lusir.factory.SingletonFactory;
import com.lusir.remoting.constants.RpcConstants;
import com.lusir.remoting.dto.RpcMessage;
import com.lusir.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author lusir
 * @date 2022/3/29 - 13:25
 **/
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;

    private final NettyClient nettyClient;

    public  NettyClientHandler() {
      this.nettyClient=SingletonFactory.getInstance(NettyClient.class);
      this.unprocessedRequests=SingletonFactory.getInstance(UnprocessedRequests.class);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                if (msg instanceof RpcMessage) {
                    RpcMessage message=(RpcMessage) msg;
                    byte messageType=message.getMessageType();
                    if (messageType== RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                            log.info("heart {}",message.getData());
                    }else if (messageType==RpcConstants.RESPONSE_TYPE) {
                        RpcResponse response=(RpcResponse) message.getData();
                        unprocessedRequests.complete(response);
                    }
                }
            }finally {
                ReferenceCountUtil.release(msg);
            }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state=((IdleStateEvent) evt).state();
            if (state==IdleState.WRITER_IDLE) {
                SocketAddress socketAddress = ctx.channel().remoteAddress();
                log.info("write will happen{}",socketAddress);
                Channel channel=nettyClient.getChannel((InetSocketAddress) socketAddress);
                RpcMessage rpcMessage=new RpcMessage();
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setData(RpcConstants.PING);
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
