package com.luzyi.dto;

import lombok.*;

/**
 * @author lusir
 * @date 2022/3/22 - 18:09
 **/
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcRequest {
    private  String interfaceName;
    private  String  methodName;
}
