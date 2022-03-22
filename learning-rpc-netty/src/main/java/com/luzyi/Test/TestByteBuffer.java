package com.luzyi.Test;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author lusir
 * @date 2022/3/22 - 19:41
 **/
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        try (FileChannel channel=new FileInputStream("D:\\git项目库\\JustCodezz-rpc-framework\\learning-rpc-netty\\data.txt").getChannel()){

            ByteBuffer buffer=ByteBuffer.allocate(10);
            while(true){

                int len= channel.read(buffer);
                if(len==-1) break;

                buffer.flip();
                while (buffer.hasRemaining()){
                    byte b=buffer.get();
                    log.info(b+"");
                }
                buffer.flip();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
