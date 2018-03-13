package com.chatroom.chatserver.parser.impl;

import com.zj.messages.Message;
import com.zj.enums.MessageTypeEnums;
import com.zj.data.ServerData;
import com.zj.util.Utils;

import java.net.Socket;

public class TextMessageHandler extends AbstractMessageHandler {

    @Override
    public boolean isSupport(Message message) {
        if(message.getType().equals(MessageTypeEnums.TEXTMSG)){
            return true;
        }
        return false;
    }

    @Override
    public void handle(Message message, Socket socket, ServerData serverData) throws Exception{
        String toUser = message.getTo();
        if(toUser.equals(Utils.ALL)){
            //发送时需要排除自己
            broadcastMessage(message,serverData);
        }else {
            if(serverData.getUserSocketMapping().get(toUser) != null){
                Socket socket1 = serverData.getUserSocketMapping().get( message.getTo());
                send(message,socket1);
            }else {
                sendNotification(false, "The selected user does not exist!",socket,serverData);
            }
        }
    }
}
