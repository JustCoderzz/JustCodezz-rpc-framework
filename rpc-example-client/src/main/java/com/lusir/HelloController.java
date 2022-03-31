package com.lusir;

import com.lusir.Test.Hello;
import com.lusir.Test.HelloService;
import com.lusir.annotation.RpcReference;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * @author lusir
 * @date 2022/3/31 - 18:52
 **/
@Component
public class HelloController {

    @RpcReference(version = "v_1",group = "test_1")
    private HelloService helloService;

    @SneakyThrows
    public void test() {
        String s = this.helloService.sayHello(new Hello("111", "222"));
        if (s.equals("Hello description is 222")) System.out.println("成功了~~~~~");
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.sayHello(new Hello("111", "222")));
        }
    }
}
