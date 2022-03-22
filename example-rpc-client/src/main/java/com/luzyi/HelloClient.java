package com.luzyi;


import com.luzyi.Bean.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author lusir
 * @date 2022/3/22 - 16:23
 **/

public class HelloClient {
    public  static  final Logger logger= LoggerFactory.getLogger(HelloClient.class);

    public Object send(Message mes,String host,int port)  {
        try{
            Socket socket=new Socket(host,port);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(mes);
            ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();

        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        HelloClient helloClient=new HelloClient();
       Message m=(Message) helloClient.send(new Message("client Mes"),"127.0.0.1",54088);
        System.out.println("client recieve" +m.getContent());
    }
}
