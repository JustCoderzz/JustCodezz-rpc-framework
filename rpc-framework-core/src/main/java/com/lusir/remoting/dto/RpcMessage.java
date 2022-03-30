package com.lusir.remoting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lusir
 * @date 2022/3/29 - 11:25
 **/
@Getter
@Setter
@Builder
public class RpcMessage {

    /**
     * 消息类型
     */
    private byte messageType;
    /**
     * 序列化类型
     */
    private byte codec;
    /**
     * 压缩方式
     */
    private byte compress;
    /**
     * 请求id
     */
    private int requestId;
    /**
     * 请求数据
     */
    private Object data;

}
