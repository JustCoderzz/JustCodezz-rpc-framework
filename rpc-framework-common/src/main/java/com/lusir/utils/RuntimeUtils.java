package com.lusir.utils;

/**
 * @author lusir
 * @date 2022/3/29 - 18:48
 **/
public class RuntimeUtils {
    public static  int getCPUs(){
        return Runtime.getRuntime().availableProcessors();
    }
}
