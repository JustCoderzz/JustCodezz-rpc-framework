package com.lusir.loadbalance;

import com.lusir.extension.SPI;
import com.lusir.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author lusir
 * @date 2022/3/29 - 20:12
 **/
@SPI
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
