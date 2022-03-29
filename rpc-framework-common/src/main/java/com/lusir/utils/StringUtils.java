package com.lusir.utils;

/**
 * @author lusir
 * @date 2022/3/29 - 16:01
 **/
public class StringUtils {

    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
