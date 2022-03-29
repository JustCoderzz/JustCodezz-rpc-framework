package com.lusir.compress;

import com.lusir.extension.SPI;

/**
 * @author lusir
 * @date 2022/3/29 - 16:01
 **/
@SPI
public interface Compress {
    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
