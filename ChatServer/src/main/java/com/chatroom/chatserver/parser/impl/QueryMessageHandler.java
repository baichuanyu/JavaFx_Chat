package com.chatroom.chatserver.parser.impl;

import com.zj.messages.Message;
import com.zj.enums.MessageTypeEnums;
import com.zj.data.ServerData;

import java.net.Socket;

public class QueryMessageHandler extends AbstractMessageHandler {

    @Override
    public boolean isSupport(Message message) {
        if(message.getType().equals(MessageTypeEnums.QUERY)){
            return true;
        }
        return false;
    }

    @Override
    public void handle(Message message, Socket socket, ServerData serverData) throws Exception{
        broadcastMessage(message,serverData);
    }
}
