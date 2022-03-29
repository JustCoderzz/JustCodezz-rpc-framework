package com.lusir.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lusir
 * @date 2022/3/29 - 13:40
 **/
public class SingletonFactory {

    private static final Map<String,Object> FACTORY_MAP=new ConcurrentHashMap<>();

    private SingletonFactory(){}


    public static <T> T getInstance(Class<T> c) {
        if (c==null) {
            throw new IllegalArgumentException();
        }
        String key=c.toString();
        if (FACTORY_MAP.containsKey(key)) {
            return c.cast(FACTORY_MAP.get(key));
        }else {
           return c.cast( FACTORY_MAP.computeIfAbsent(key,k->{
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }

}
