package com.luzyi.codec;

import com.luzyi.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.AllArgsConstructor;

/**
 * @author lusir
 * @date 2022/3/26 - 19:57
 **/
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;

    private final Class<?> genericClass;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)) {
             byte[] bytes = serializer.serialize(o);

             int length=bytes.length;

             byteBuf.writeInt(length);

             byteBuf.writeBytes(bytes);
        }


    }
}
