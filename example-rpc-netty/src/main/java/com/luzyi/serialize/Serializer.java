package com.luzyi.serialize;

/**
 * @author lusir
 * @date 2022/3/26 - 19:00
 **/
public interface Serializer {
    /**
     * 序列话的方法
     * @param obj  序列化对象
     * @return 返回序列化后的字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化的方法
     * @param bytes  字节流
     * @param clazz  序列化后的类
     * @param <T>
     * @return    序列化后的对象
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
