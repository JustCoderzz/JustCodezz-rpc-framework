package com.luzyi.Test;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
@author lusir
@date 2022/3/22 - 20:10
**/
@Slf4j
public class BlockingServer {

    public static void main(String[] args)  throws Exception{

        ServerSocketChannel  ssc=ServerSocketChannel.open();
//        使得ServerSocketChannel模式为非阻塞模式
        ssc.configureBlocking(false);//
        ByteBuffer buffer=ByteBuffer.allocate(16);

        ssc.bind(new InetSocketAddress(8080));
        log.info("listen....");
        List<SocketChannel> list=new ArrayList<>();
        while(true){
            log.info("connected");
//          开启非阻塞模式后 如果没有连接建立   scc会返回null
            SocketChannel socketChannel=ssc.accept(); //acept 方法会阻塞
            if(socketChannel!=null){
                socketChannel.configureBlocking(false);//设置SocketChannel 为非阻塞模式
                list.add(socketChannel);
            }

            for(SocketChannel channel:list){
//                开启SocketChannel的非阻塞模式以后，该方法若没有接受到数据  会返回0
                int read=  channel.read(buffer); //read 方法会阻塞
                if(read>0){
                    buffer.flip();
                    while (buffer.hasRemaining()){
                        byte b=buffer.get();
                        log.info(b+"");
                    }
                    buffer.clear();
                    log.info("after connect");
                }

            }

        }
    }
}
