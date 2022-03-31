package com.luzyi;

import java.lang.reflect.Field;

/**
 * @author lusir
 * @date 2022/3/31 - 16:47
 **/
public class ReflectTest {

    int a=0;
    String string=new String("123");

    public static void main(String[] args) {
        ReflectTest o=new ReflectTest();
        Field[] fields = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }
    }
}
