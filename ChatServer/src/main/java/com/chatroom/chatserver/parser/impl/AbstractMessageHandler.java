package com.chatroom.chatserver.parser.impl;

import com.alibaba.fastjson.JSONObject;
import com.chatroom.chatserver.parser.MessageHandler;
import com.zj.data.ServerData;
import com.zj.enums.MessageTypeEnums;
import com.zj.messages.Message;
import com.zj.messages.TextMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessageHandler implements MessageHandler {
    /**
     * 将message转化为字符串后发送指定的客户端
     * @param message
     */
    protected void send(Message message,Socket socket) throws IOException {
        //转化为gson的字符串
        String messagesString = JSONObject.toJSONString(message);
        PrintStream ps = new PrintStream( socket.getOutputStream());
        //发送消息，注意消息格式为(messageString + "\n")
        ps.println(messagesString);
    }

    /**
     * 将message转化为字符串后发送给所有的客户端
     * @param message
     */
    protected void broadcastMessage(Message message,ServerData serverData) throws IOException{
        //转化为gson的字符串

        if(serverData.getUserSocketMapping().isEmpty()){
            return;
        }

        Map<String,Socket> map = serverData.getUserSocketMapping();
        map.forEach((key,socket) -> {
            try {
                message.setTo(key);
                PrintStream ps = null;
                String messagesString = JSONObject.toJSONString(message);
                ps = new PrintStream(socket.getOutputStream());
                //发送消息，注意消息格式为(messageString + "\n")
                ps.println(messagesString);
            }catch (Exception e){

            }
        });


    }

    protected void broadcastMessageWithout(Message message, List<String> exceptUserList,ServerData serverData) throws Exception{
        //转化为gson的字符串
        String messagesString = JSONObject.toJSONString(message);
        if(serverData.getUserSocketMapping().isEmpty()){
            return;
        }
        if(exceptUserList == null || exceptUserList.size() ==0){
            this.broadcastMessage(message,serverData);
        } else {
            serverData.getUserSocketMapping().forEach((username,socket)-> {
                if (!exceptUserList.contains(username)) {
                    try {
                        PrintStream ps = new PrintStream(socket.getOutputStream());
                        //发送消息，注意消息格式为(messageString + "\n");
                        ps.println(messagesString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 发送在线用户信息集
     * @throws IOException
     */
    protected void broadcastUserList(ServerData serverData) throws IOException{
        //无在线用户则不发送
        if(serverData.getUserList().isEmpty()){
            return;
        }
        //创建用户集反馈信息
        Message uResult = new Message();
        uResult.setType(MessageTypeEnums.USERLIST);
        uResult.setUserlist(serverData.getUserList());
        broadcastMessage(uResult,serverData);
        //logger.info("用户集更新成功！");
    }

    /**
     * 发送通知消息，用于通知全体用户或当前用户
     * @param isAllUsers
     * @param notice
     * @throws IOException
     */
    protected void sendNotification(boolean isAllUsers, String notice,Socket socket,ServerData serverData) throws IOException {
        TextMessage message = new TextMessage();
        message.setType(MessageTypeEnums.NOTIFICATION);
        message.setContent(notice);
        if(isAllUsers){
          broadcastMessage(message,serverData);
        }else {
            send(message,socket);
        }
    }

}
