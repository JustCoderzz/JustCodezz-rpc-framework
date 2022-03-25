package com.luzyi.netty;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutionException;


/**
 * @author lusir
 * @date 2022/3/25 - 15:17
 **/
public class TestPromiseFuture {
    public static void main(String[] args) {
        DefaultEventLoop eventExecutors=new DefaultEventLoop();
        DefaultPromise<Integer> promise=new DefaultPromise<>(eventExecutors);

        new Thread(()->{
            try {
                Thread.sleep(1000);
                promise.setSuccess(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
            System.out.println(1);

        }).start();

        try {
            promise.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
