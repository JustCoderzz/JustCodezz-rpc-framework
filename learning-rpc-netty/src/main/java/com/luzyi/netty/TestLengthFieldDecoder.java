package com.luzyi.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author lusir
 * @date 2022/3/25 - 18:40
 **/
public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        EmbeddedChannel channel=new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,0,4,0,0),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer();
        send(buf,"hello world");
        send(buf,"hi");
        channel.writeInbound(buf);

    }
    public  static  void send(ByteBuf buffer,String mes) {

         byte[] bytes = mes.getBytes();
         int length=bytes.length;
         buffer.writeInt(length);
         buffer.writeBytes(bytes);
    }
}
