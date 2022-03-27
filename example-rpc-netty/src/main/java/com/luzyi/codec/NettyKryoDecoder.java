package com.luzyi.codec;

import com.luzyi.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author lusir
 * @date 2022/3/26 - 22:39
 **/
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private  final Serializer serializer;
    private  final Class<?> clazz;

    private static final int BODY_LENGTH=4;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes()>=BODY_LENGTH) {
            in.markReaderIndex();
            int dataLength=in.readInt();

            if (dataLength<0 || in.readableBytes()<0) {
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            if (in.readableBytes()<dataLength) {
                in.resetReaderIndex();
                return;
            }
            byte [] bytes=new byte[dataLength];
            in.readBytes(bytes,0,dataLength);

             Object obj = serializer.deserialize(bytes, clazz);
             out.add(obj);
             log.info("successful!");
        }


    }
}
