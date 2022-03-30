package com.lusir.remoting.transport.netty.server;

import com.lusir.enums.CompressTypeEnum;
import com.lusir.enums.RpcResponseCodeEnum;
import com.lusir.enums.SerializationTypeEnum;
import com.lusir.factory.SingletonFactory;
import com.lusir.remoting.constants.RpcConstants;
import com.lusir.remoting.dto.RpcMessage;
import com.lusir.remoting.dto.RpcRequest;
import com.lusir.remoting.dto.RpcResponse;
import com.lusir.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lusir
 * @date 2022/3/30 - 15:26
 **/
@Slf4j
public class NettyRpcServiceHandler extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServiceHandler() {
        this.rpcRequestHandler= SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("server receive msg: [{}] ", msg);
                byte messageType=((RpcMessage) msg).getMessageType();
                RpcMessage message=new RpcMessage();
                message.setCodec(SerializationTypeEnum.KYRO.getCode());
                message.setCompress(CompressTypeEnum.GZIP.getCode());
                if (messageType== RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    message.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    message.setData(RpcConstants.PONG);
                }else {
                   RpcRequest rpcRequest =(RpcRequest ) ((RpcMessage) msg).getData();
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    message.setMessageType(RpcConstants.RESPONSE_TYPE);
                    if (ctx.channel().isActive()&&ctx.channel().isWritable()){
                        RpcResponse<Object> response=RpcResponse.success(result,rpcRequest.getQuestId());
                        message.setData(response);
                    }else {
                        RpcResponse<Object> response= RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        message.setData(response);
                        log.error("not writable now, message dropped");
                    }

                }
                ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state==IdleState.READER_IDLE) {
                ctx.close();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
