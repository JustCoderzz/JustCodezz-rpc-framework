package com.lusir.remoting.transport.netty.codec;

import com.lusir.compress.Compress;
import com.lusir.enums.CompressTypeEnum;
import com.lusir.enums.SerializationTypeEnum;
import com.lusir.extension.ExtensionLoader;
import com.lusir.remoting.constants.RpcConstants;
import com.lusir.remoting.dto.RpcMessage;
import com.lusir.remoting.dto.RpcRequest;
import com.lusir.remoting.dto.RpcResponse;
import com.lusir.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author lusir
 * @date 2022/3/29 - 16:34
 **/
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
    public RpcMessageDecoder(){
        this(RpcConstants.MAX_FRAME_LENGTH,5,4,-9,0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
       Object decoded= super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    public  Object decodeFrame(ByteBuf buf) {
        checkMagicNumber(buf);
        checkVersion(buf);
        int fullLength=buf.readInt();
        byte messageType=buf.readByte();
        byte codec=buf.readByte();
        byte compress=buf.readByte();
        int requestId=buf.readInt();
        RpcMessage rpcMessage=new RpcMessage();
        rpcMessage.setCompress(compress);
        rpcMessage.setCodec(codec);
        rpcMessage.setRequestId(requestId);
        rpcMessage.setMessageType(messageType);
        if (messageType==RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType==RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int len=fullLength-RpcConstants.HEAD_LENGTH;
        if (len>0) {
            byte[] bytes=new byte[len];
            buf.readBytes(bytes);
            String compressName= CompressTypeEnum.getName(compress);
            Compress cPress=ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bytes= cPress.decompress(bytes);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(SerializationTypeEnum.getName(codec));
            if (messageType==RpcConstants.REQUEST_TYPE) {
                RpcRequest request = serializer.deserialize(bytes, RpcRequest.class);
                rpcMessage.setData(request);
            }else {
                RpcResponse response = serializer.deserialize(bytes, RpcResponse.class);
                rpcMessage.setData(response);
            }
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // read the first 4 bit, which is the magic number, and compare
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
