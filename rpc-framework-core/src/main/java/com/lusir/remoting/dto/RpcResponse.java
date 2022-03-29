package com.lusir.remoting.dto;

import com.lusir.enums.RpcResponseCodeEnum;
import com.sun.org.apache.regexp.internal.RE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lusir
 * @date 2022/3/29 - 12:07
 **/
@Setter
@Getter
public class RpcResponse<T> implements Serializable {
        private static final  long serialVersionUID = -3441122277026618918L;

        private String requestId;

        private Integer code;

        private  String message;

        private T data;

        public static <T> RpcResponse<T> success(T data,String requestId) {
                RpcResponse<T> response=new RpcResponse<>();
                response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
                response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
                response.setRequestId(requestId);
                if (data!=null) {
                        response.setData(data);
                }
                return response;
        }

        public  static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
                RpcResponse<T> response=new RpcResponse<>();
                response.setMessage(rpcResponseCodeEnum.getMessage());
                response.setCode(rpcResponseCodeEnum.getCode());
                return response;
        }
}
