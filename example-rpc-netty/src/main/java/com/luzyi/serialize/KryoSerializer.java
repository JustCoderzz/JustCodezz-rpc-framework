package com.luzyi.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.luzyi.dto.RpcRequest;
import com.luzyi.dto.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author lusir
 * @date 2022/3/26 - 19:07
 **/
public class KryoSerializer implements  Serializer{

    private  static  final ThreadLocal<Kryo> KRYO_THREAD_LOCAL=ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);// 支持循环引用 配置成false 速度会更快 但是遇到循环引用的话 就会出现栈内存溢出
        kryo.setRegistrationRequired(false);//关闭注册行为  因为不能保证每一台机器上注册编号保持一致  所以多机部署会出现问题
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            Output output=new Output(byteArrayOutputStream)) {
            Kryo kryo=KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output,obj);
            KRYO_THREAD_LOCAL.remove();
            return output.toBytes();
        }catch (Exception e) {
            throw new SerializerException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
            Input input=new Input(byteArrayInputStream)) {
            Kryo kryo=KRYO_THREAD_LOCAL.get();
            Object o=kryo.readObject(input,clazz);
            KRYO_THREAD_LOCAL.remove();
            return clazz.cast(o);
        }catch (Exception e) {
            throw new SerializerException("反序列化失败");
        }
    }
}
