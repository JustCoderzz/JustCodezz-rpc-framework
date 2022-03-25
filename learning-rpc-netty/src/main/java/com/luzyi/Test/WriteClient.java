package com.luzyi.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author lusir
 * @date 2022/3/24 - 18:22
 **/
public class WriteClient {
    public static void main(String[] args) throws  Exception{

        SocketChannel sc=SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));

        int count=0;
        while (true) {
            ByteBuffer buffer=ByteBuffer.allocate(1024*1024);
            count+=sc.read(buffer);
            System.out.println(count);
            buffer.clear();
        }

    }
}
