package com.lusir.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lusir
 * @date 2022/3/29 - 12:13
 **/
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
