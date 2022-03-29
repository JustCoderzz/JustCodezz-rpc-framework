package com.lusir.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author lusir
 * @date 2022/3/29 - 13:58
 **/
public class RpcConstants {

     //魔数
    public static  final byte[] MAGIC_NUMBER={(byte) 'J',(byte) 'R',(byte) 'P',(byte) 'C'};
    //默认字符集
    public static final Charset DEFAULT_CHARSET= StandardCharsets.UTF_8;
    //版本信息
    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 16;
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;


}
