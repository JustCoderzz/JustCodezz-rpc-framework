package com.luzyi.Proxy;

/**
 * @author lusir
 * @date 2022/3/27 - 14:06
 **/
public class StaticProxy {
    interface  SendService{
        void sendMes();
    }

   static class SendServiceImpl implements SendService{
        @Override
        public void sendMes() {
            System.out.println("发送消息");
        }
    }

   static class SendProxy implements SendService {

        SendService sendService;
        SendProxy(SendService sendService) {
            this.sendService=sendService;
        }

        @Override
        public void sendMes() {
            System.out.println("发送消息前增强功能");
            sendService.sendMes();
            System.out.println("发送消息后增强功能");
        }
    }

    public static void main(String[] args) {
        SendService sendService=new SendServiceImpl();
        SendProxy sendProxy=new SendProxy(sendService);
        sendProxy.sendMes();
    }
}
