package com.lusir.remoting.transport;

import com.lusir.extension.SPI;
import com.lusir.remoting.dto.RpcRequest;

/**
 * @author lusir
 * @date 2022/3/29 - 12:32
 **/
@SPI
public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
