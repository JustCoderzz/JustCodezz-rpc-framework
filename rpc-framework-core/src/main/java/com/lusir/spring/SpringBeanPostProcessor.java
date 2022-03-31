package com.lusir.spring;

import com.lusir.annotation.RpcReference;
import com.lusir.annotation.RpcService;
import com.lusir.config.RpcServiceConfig;
import com.lusir.extension.ExtensionLoader;
import com.lusir.extension.SPI;
import com.lusir.factory.SingletonFactory;
import com.lusir.provider.ServiceProvider;
import com.lusir.provider.impl.ZkServiceProviderImpl;
import com.lusir.proxy.RpcClientProxy;
import com.lusir.remoting.transport.RpcRequestTransport;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author lusir
 * @date 2022/3/31 - 16:21
 **/
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            RpcService rpcService=bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig rpcServiceConfig=RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class targetClass=bean.getClass();
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field:fields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference!=null) {
                RpcServiceConfig rpcServiceConfig=RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy=new RpcClientProxy(rpcServiceConfig,rpcClient);
                Object proxy=rpcClientProxy.getProxy(field.getType());
                field.setAccessible(true);
                try {
                    field.set(bean,proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
