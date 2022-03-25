package com.luzyi.MultiThread;

import sun.dc.pr.PRError;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lusir
 * @date 2022/3/24 - 19:44
 **/
public class MultiThreadServer {
    public static void main(String[] args) throws Exception {
        Thread.currentThread().setName("Boss");
        ServerSocketChannel scc=ServerSocketChannel.open();
        scc.configureBlocking(false);

        Selector boss=Selector.open();

        scc.register(boss, SelectionKey.OP_ACCEPT);

        scc.bind(new InetSocketAddress(8080));
        Worker workers[]=new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i]=new Worker("Worker-"+i);
        }

        AtomicInteger index=new AtomicInteger();
        while (true) {
            boss.select();

            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            if (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = scc.accept();
                    sc.configureBlocking(false);
                    workers[index.getAndIncrement()%workers.length].register(sc);
                }
            }
        }
    }

    static  class Worker implements  Runnable{
        private  Thread thread;
        private  String name;
        private  Selector selector;
        private  volatile boolean start=false;
        private ConcurrentLinkedQueue<Runnable> queue=new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }
        public  void register(SocketChannel sc) throws  Exception{
            if (!start){
                this.thread=new Thread(this);
                thread.start();
                selector=Selector.open();
                start=true;
            }
//            向队列添加了任务
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();
//            sc.register(selector,SelectionKey.OP_READ);  直接这样写也可以
        }

        @Override
        public void run() {

            while (true) {
                try {
                    selector.select();
                    Runnable task=queue.poll();
                    if(task!=null) task.run();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            SocketChannel channel =(SocketChannel) key.channel();
                            channel.configureBlocking(false);
                            ByteBuffer buffer=ByteBuffer.allocate(16);
                            channel.read(buffer);
                            buffer.flip();
                            while (buffer.hasRemaining()){
                                System.out.println(buffer.get());
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
