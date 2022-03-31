package com.lusir.HelloService;

import com.lusir.Test.Hello;
import com.lusir.Test.HelloService;
import com.lusir.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lusir
 * @date 2022/3/31 - 19:09
 **/
@Slf4j
@RpcService(group = "test_1",version = "v_1")
public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl被创建!");
    }
    @Override
    public String sayHello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
