package com.luzyi.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lusir
 * @date 2022/3/27 - 14:15
 **/
public class DynamicProxy {
    interface  SendService{
        void sendMes();
    }

    static class SendServiceImpl implements SendService {
        @Override
        public void sendMes() {
            System.out.println("发送消息");
        }
    }

   static class MyInvocationHandler implements InvocationHandler {

        private  final Object target;
        MyInvocationHandler(Object o) {
            this.target=o;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            System.out.println("before method:"+method.getName());
            method.invoke(target,args);
            System.out.println("after method:"+method.getName());
            return null;
        }
    }

  static   class JdkProxyFactory {
        public  static  Object getProxy(Object target) {
            return Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    new MyInvocationHandler(target)
            );
        }
    }

    public static void main(String[] args) {
        SendService sendService=(SendService) JdkProxyFactory.getProxy(new SendServiceImpl());
        sendService.sendMes();
    }

}
