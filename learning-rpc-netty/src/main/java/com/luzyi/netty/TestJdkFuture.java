package com.luzyi.netty;

import java.util.concurrent.*;

/**
 * @author lusir
 * @date 2022/3/25 - 14:58
 **/
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

         ExecutorService service = Executors.newFixedThreadPool(2);
         Future<Integer> res = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 10;
            }
        });

         res.get();//会阻塞住直到拿到结果

    }
}
