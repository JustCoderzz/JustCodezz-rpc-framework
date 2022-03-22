package com.luzyi.dto;

import lombok.*;

/**
 * @author lusir
 * @date 2022/3/22 - 18:10
 **/
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcResponse {
    private  String message;
}
