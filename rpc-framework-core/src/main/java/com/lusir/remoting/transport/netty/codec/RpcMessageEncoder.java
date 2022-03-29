package com.lusir.remoting.transport.netty.codec;

import com.lusir.compress.Compress;
import com.lusir.enums.CompressTypeEnum;
import com.lusir.enums.SerializationTypeEnum;
import com.lusir.extension.ExtensionLoader;
import com.lusir.remoting.constants.RpcConstants;
import com.lusir.remoting.dto.RpcMessage;
import com.lusir.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lusir
 * @date 2022/3/29 - 15:06
 **/
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static  final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf byteBuf) throws Exception {
        try {
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RpcConstants.VERSION);
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            byte messageType = msg.getMessageType();
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType
                    != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
//            serialize
                String codecName = SerializationTypeEnum.getName(msg.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
                bodyBytes = serializer.serialize(msg.getData());
//            compress
                String compressName = CompressTypeEnum.getName(msg.getCompress());
                log.info("compress name: [{}] ", codecName);
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }
            byteBuf.writeInt(fullLength);
            byteBuf.writeByte(msg.getMessageType());
            byteBuf.writeByte(msg.getCodec());
            byteBuf.writeByte(msg.getCompress());
            byteBuf.writeByte(ATOMIC_INTEGER.incrementAndGet());
            if (bodyBytes != null) {
                byteBuf.writeBytes(bodyBytes);
            }
        }catch (Exception e) {
            log.error("Encode request error!", e);
        }
    }
}
