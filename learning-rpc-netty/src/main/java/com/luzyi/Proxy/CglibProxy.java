package com.luzyi.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author lusir
 * @date 2022/3/27 - 14:37
 **/
public class CglibProxy {

    public class Service {

        public String send(String message) {
            System.out.println("send message:" + message);
            return message;
        }
    }

    static  class MyMethodInterceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            System.out.println("before method:"+method.getName());
            Object obj = methodProxy.invokeSuper(o, args);
            System.out.println("after method:"+method.getName());
            return obj;
        }
    }

    static class CglibProxyFactory {
        public static Object getProxy(Class<?> clazz) {
            Enhancer enhancer=new Enhancer();
            enhancer.setClassLoader(clazz.getClassLoader());
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(new MyMethodInterceptor());
            return enhancer.create();
        }
    }

    public static void main(String[] args) {
        Service proxy = (Service) CglibProxyFactory.getProxy(Service.class);
        proxy.send("hello");
    }
}
