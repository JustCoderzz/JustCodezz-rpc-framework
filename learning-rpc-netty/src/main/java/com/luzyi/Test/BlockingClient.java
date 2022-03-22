package com.luzyi.Test;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author lusir
 **/
public class BlockingClient {
    public static void main(String[] args) throws  Exception {
        SocketChannel sc=SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        System.out.println("waiting...");
        System.in.read();
    }
}
