package com.chatroom.chatserver.parser.impl;

import com.zj.messages.Message;
import com.zj.enums.MessageTypeEnums;
import com.zj.data.ServerData;
import com.zj.data.UserInfo;

import java.net.Socket;

public class DisConnectionHandler extends AbstractMessageHandler {


    @Override
    public boolean isSupport(Message message) {
        if(message.getType().equals(MessageTypeEnums.LOGOUT)){
            return true;
        }
        return false;
    }

    @Override
    public void handle(Message message, Socket socket, ServerData serverData) throws Exception{

        String userName = message.getFrom();
        socket = serverData.getUserSocketMapping().get(userName);
        if(socket != null){
            serverData.getUserSocketMapping().remove(userName);
            for(UserInfo user : serverData.getUserList()){
                if(user.getUsername().equals(userName)){
                    serverData.getUserList().remove(user);
                    break;//注意缺少break会出bug，因为remove后size的值改变了，遍历中ArrayList不可变。使用Iterator的方式也可以顺利删除和遍历。
                }
            }
        }
        //创建用户集反馈信息
        broadcastUserList(serverData);
        //发送用户下线通知
        sendNotification(true, userName + " offline..",socket,serverData);
    }
}
