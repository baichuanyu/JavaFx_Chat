package com.chatroom.chatserver.parser;

import com.zj.messages.Message;
import com.zj.data.ServerData;

import java.net.Socket;

public interface MessageHandler {

    public boolean isSupport(Message message);
    public void handle(Message message, Socket socket, ServerData serverData) throws Exception;
}
