package com.luzyi;

import com.luzyi.Bean.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author lusir
 * @date 2022/3/22 - 16:43
 **/
public class HelloServer {
    public final Logger logger= LoggerFactory.getLogger(HelloServer.class);

    public  void start(int port){
        try {
            ServerSocket socket=new ServerSocket(port);
            Socket server;
            while((server=socket.accept())!=null){
                logger.info("client connected");
                try {
                    ObjectInputStream objectInputStream=new ObjectInputStream(server.getInputStream());
                    ObjectOutputStream objectOutputStream=new ObjectOutputStream(server.getOutputStream());
                    Message message=(Message) objectInputStream.readObject();
                    logger.info("server accept");
                    System.out.println(message.getContent());
                    message.setContent("Server Mes");
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
                }catch ( Exception e){
                    logger.info(e.getMessage());
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        HelloServer helloServer=new HelloServer();
        helloServer.start(54088);
    }
}
