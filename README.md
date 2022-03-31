# JustCodezz-rpc-framework
## 技术栈

1. netty实现网络传输
2. kryo实现序列化
3. jdk动态代理
4. zookeeper服务注册中心
5. SPI机制（Service Provider Interface）
6. Spring注解实现服务注册、服务消费
7. 使用一致性哈希环来实现负载均衡

## 项目总结

经过这一整个项目的一步一步的搭建完成，我有了更多的认识与思考：

RPC(Remote Procedure Call)远程过程调用主要解决的就是运行在不同主机上的项目无法直接调用远程的服务，通过RPC使得本地调用远程服务跟在本地调用一样。

**网络传输**

我需要调什么方法，参数是什么，返回类型是什么我需要告诉服务端。从而这就牵扯到我需要使用网络传输的内容，但是传统的Socket是BIO的，会使得处理请求阻塞，并且功能单一。NIO可以使用selector来解决该问题，让一个selector监听多个channel，但是直接使用NIO的话编程难度太大，需要处理的细节太多，比如Bytebuffer的读写模式切换、自动扩容、判断事件类型处理完后还需要移除selectionkey，否则会空指针异常等等。

所以我们采用基于NIO多路复用的netty，它封装好了多种处理器，开箱即用，基于心跳检测的IdleStateHandler，基于解决TCP拆包粘包问题的LengthFieldBasedFrameDecoder。

**序列化和反序列化**

网络传输就必须需要序列化，将对象转换为二进制流，返回到达服务端在反序列化位对象，拿到其中的数据。

主要使用的kryo

**动态代理**

通过动态代理我们可以屏蔽方法调用的底层细节。用户只需要知道使用该对象的某个方法，然后得到了某个结果就行，不用知道该对象的功能。我们可以通过动态代理，给每一个调用的类在初始化时将一个增强的代理对象给他，在调用方法时，只需要进行网络传输请求，并把返回的结果返回就行。

**传输协议**

通过传输协议我们可以正确的取出我们传输请求的数据，也可以起到验证、纠正的作用。我们定义需要传输哪些类型的数据， 并且还会规定每一种类型的数据应该占多少字节。这样我们在接收到二进制数据之后，就可以正确的解析出我们需要的数据。

一般协议需要包含以下内容：

1. 魔数。起到验证的作用，筛选服务端的数据包。
2. 序列化方式
3. 消息类型
4. 数据段长度
5. 压缩方式
6. 请求序列号

## 项目运行过程

1. 因为我们导入了Spring-context的包，并且自定义了注解。Server端运行时回去装载bean，包含的有component注解的bean，RpcService的类
2. 因为我们自定义了SpringBeanPostProcessor实现的时BeanPostProcessor，所以在bean实例化时我们使用serviceProvider的publishService方法中去调用了zk的registry类去完成了在zookeep中服务的注册
3. 服务端通过从容器中拿到NettyServer对象来开启服务
4. client端中我们将一个需要使用的方法的类用component注解来声明，该类被容器初始化时该字段有一个注解为RpcReference，就是让SpringBeanPostProcessor来消费服务，通过方法可以将其中的方法对象替换成我们的代理对象，调用该对象的方法即为调用代理对象增强的方法
5. 调用方法时，触发代理对象的invoke方法时，使用netty传输我们需要调用的方法后相关参数给远程服务，通过编码，序列化，传输给远程服务，远程解码，反序列化，拿到请求rpcRequest包
6. 在处理中我们在serviceProvider中找到我们需要的service对象，这个对象是服务端注册服务时，我们就将该服务对象放进了serviceproviderImpl的map中，我们只需要根据RpcRequest的RpcServiceName找到对应的Bean。
7. 然后我们使用RequestHandler的handle方法来使用该Bean来执行对应的方法得到对应的结果。我们将返回的result放进RpcResponse对象封装成RpcMessage对象来返回给调用者。
8. 调用者拿到RpcMessage从中取出Data返回给调用者。
