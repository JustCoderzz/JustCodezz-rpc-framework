package com.luzyi.Test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author lusir
 * @date 2022/3/22 - 20:48
 **/
@Slf4j
public class SelectorServer {

//    按照\n进行分割数据包
    public  static  void split(ByteBuffer source){
        source.flip();
        for (int i=0;i<source.limit();i++) {
            if (source.get(i)=='\n'){
                int length=i+1-source.position();
                for(int j=0;j<length;j++){
                    source.put(source.get());
                }
            }
        }
        source.compact();

    }
    public static void main(String[] args) throws  Exception{
//        创建Selector
        Selector selector=Selector.open();
//        获取ServerSocketChannel
        ServerSocketChannel ssc=ServerSocketChannel.open();
//        设置为非阻塞模式
        ssc.configureBlocking(false);
//        注册channel 获得key
        SelectionKey ssKey = ssc.register(selector, 0, null);
//        设置该key关注的事件类型
        ssKey.interestOps(SelectionKey.OP_ACCEPT);
//        绑定端口
        ssc.bind(new InetSocketAddress(8080));

        while (true) {
//            selector监听事件
//            select 在有未处理事件时是不会阻塞的   也就是认为你没有处理完 还会加入到集合中
            selector.select();
//            获取事件集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//            遍历集合  处理请求
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();

//                selector会在发生事件后，像集合中加入key  但是不会删除  所以下次循环遍历时  会返回NullPointerExecption  需要我们手动删除
                iterator.remove();
//                将把下面处理事件的代码注释后  会发现 select 方法又到达了非阻塞  会一直while循环  这就相当于只有你执行完后  selector才会觉得没事可干
//                就等待了  否则就一直做 直到事件做完  或者调用cancel方法
//                区分事件
                if (key.isAcceptable()){
                    ServerSocketChannel channel =(ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer=ByteBuffer.allocate(16);//attatement
//                    将一个bytebuffer 作为附件关联到selectionkey上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
//                事件取消
                    key.cancel();
                    log.info("scc:{}"+sc);
                }else if (key.isReadable()){
                    try {
                        SocketChannel cha=(SocketChannel) key.channel();
//                        获取key上关联的附件
                        ByteBuffer buffer=(ByteBuffer) key.attachment();
                        int read = cha.read(buffer);//如果客户端是正常断开的话  read的返回值是-1
                        if(read==-1){
                            key.cancel();
                        }else{
//                            buffer.flip();
//                            while (buffer.hasRemaining()){
//                                byte b=buffer.get();
//                                log.info(b+"");
//                            }
                            split(buffer);
                            if(buffer.position()==buffer.limit()){
                                ByteBuffer newBuffer=ByteBuffer.allocate(buffer.capacity()*2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }

                            System.out.println(Charset.defaultCharset().decode(buffer).toString());
                        }

                    } catch (IOException e){
                        e.printStackTrace();
                        key.cancel();//因为客户端断开了,所以需要将key取消掉 也就是永久删除
                    }


                }


            }
        }
    }
}
