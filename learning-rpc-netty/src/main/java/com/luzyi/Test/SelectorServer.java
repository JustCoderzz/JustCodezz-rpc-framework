package com.luzyi.Test;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author lusir
 * @date 2022/3/22 - 20:48
 **/
@Slf4j
public class SelectorServer {
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
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
//                将下面三行代码注释后  会发现 select 方法又到达了非阻塞  会一直while循环  这就相当于只有你执行完后  selector才会觉得没事可干
//                就等待了  否则就一直做 直到事件做完  或者调用cancel方法
                ServerSocketChannel channel =(ServerSocketChannel) key.channel();

                SocketChannel sc = channel.accept();
//                事件取消
                key.cancel();
                log.info("scc:{}"+sc);

            }
        }
    }
}
