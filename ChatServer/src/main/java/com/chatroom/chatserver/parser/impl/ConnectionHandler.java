package com.chatroom.chatserver.parser.impl;

import com.zj.messages.ConnectMessage;
import com.zj.messages.Message;
import com.zj.enums.MessageTypeEnums;
import com.zj.data.ServerData;

import java.net.Socket;

public class ConnectionHandler extends AbstractMessageHandler {

    @Override
    public boolean isSupport(Message message) {
        if(message.getType().equals(MessageTypeEnums.LOGIN)){
            return true;
        }
        return false;
    }

    @Override
    public void handle(Message message, Socket socket,ServerData serverData) throws Exception{
        String username = message.getFrom();
        //监测用户名是否已存在
        ConnectMessage respMessage = new ConnectMessage();
        if(!serverData.getUserSocketMapping().containsKey(username)){
            serverData.getUserSocketMapping().put(username, socket);
            serverData.getUserList().addAll(message.getUserlist());
            //创建连接成功反馈信息
            respMessage.setType(MessageTypeEnums.SUCCESS);
            respMessage.setMsg(username + " connect successful!");
            //发送
            send(respMessage,socket);
            //创建用户集反馈信息
            broadcastUserList(serverData);
            //发送新用户上线通知
            sendNotification(true, username +" online..",socket,serverData);
        }else {
            respMessage.setType(MessageTypeEnums.FAIL);
            respMessage.setMsg(message.getFrom() + "is existed, connect failed!");
            send(respMessage,socket);
        }

    }
}
