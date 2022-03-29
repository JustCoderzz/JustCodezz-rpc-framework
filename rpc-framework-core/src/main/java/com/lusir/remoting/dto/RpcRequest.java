package com.lusir.remoting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lusir
 * @date 2022/3/29 - 11:17
 **/
@Getter
public class RpcRequest implements Serializable {
    private static  final  long serialVersionUID= -2501020588965315545L;
    private String questId;
    private String interfaceName;
    private  String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
//    RpcMessage rpcMessage;
    private String version;
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}

