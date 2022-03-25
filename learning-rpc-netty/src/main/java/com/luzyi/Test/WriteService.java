package com.luzyi.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lusir
 * @date 2022/3/24 - 18:13
 **/
public class WriteService {
    public static void main(String[] args) throws  Exception{

        ServerSocketChannel scc=ServerSocketChannel.open();
        scc.configureBlocking(false);

        Selector selector=Selector.open();

        scc.register(selector, SelectionKey.OP_ACCEPT);

        scc.bind(new InetSocketAddress(8080));

        while (true){
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            if (iterator.hasNext()) {
                SelectionKey key=iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc=channel.accept();
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    sc.configureBlocking(false);
                    StringBuilder sb=new StringBuilder();
//                    客户端发送大量的数据
                    for(int i=0;i<30000000;i++){
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());

//                    返回实际写入的字节数
                    int write=sc.write(buffer);
                    System.out.println(write);
//                    违背了异步的原则  会一直阻塞在这里直到buffer的数据写完  我们希望可以让写的时候别人可以读
//                    while (buffer.hasRemaining()){
//                        int write = sc.write(buffer);
//                        System.out.println(write);
//                    }
//                    如果还有剩余
                    if (buffer.hasRemaining()){
//                      关注可写事件
                        scKey.interestOps(key.interestOps()+SelectionKey.OP_WRITE);
//                        把未写完的数据挂在到key上
                        scKey.attach(buffer);
                    }

                }else if (key.isWritable()){

                    ByteBuffer buffer=(ByteBuffer)key.attachment();
                    SocketChannel sc =(SocketChannel) key.channel();
                    sc.write(buffer);
                    if (!buffer.hasRemaining()){
                        key.attach(null);//需要清理附件
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);//不需要关注可写事件
                    }
                }
            }
        }
    }
}
