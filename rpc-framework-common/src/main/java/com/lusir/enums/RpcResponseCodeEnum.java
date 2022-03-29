package com.lusir.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * @author lusir
 * @date 2022/3/29 - 12:13
 **/
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {

    SUCCESS(200,"Your remote call is successful"),
    FAIL(500,"The remote call is fail");
    private final int code;
    private final String message;
}
