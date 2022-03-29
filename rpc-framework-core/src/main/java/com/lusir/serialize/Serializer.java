package com.lusir.serialize;

import com.lusir.extension.SPI;

/**
 * @author lusir
 * @date 2022/3/29 - 15:52
 **/
@SPI
public interface Serializer {

    byte[] serialize(Object o);

    <T> T deserialize(byte [] bytes,Class<T> clazz);
}
