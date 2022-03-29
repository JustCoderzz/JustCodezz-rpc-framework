package com.lusir.utils;

import java.util.List;

/**
 * @author lusir
 * @date 2022/3/29 - 20:16
 **/
public class CollectionUtils {
    public static  boolean isEmpty(List list){
        if (list==null||list.isEmpty()) {
            return true;
        }
        return false;
    }
}
