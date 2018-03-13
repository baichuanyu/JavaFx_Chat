package com.chatroom.chatserver.parser;

import com.chatroom.chatserver.parser.impl.ConnectionHandler;
import com.chatroom.chatserver.parser.impl.DisConnectionHandler;
import com.chatroom.chatserver.parser.impl.QueryMessageHandler;
import com.chatroom.chatserver.parser.impl.TextMessageHandler;
import com.zj.messages.Message;
import com.zj.data.ServerData;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MessageParser {

    static List<MessageHandler>  list = new ArrayList<>();

    private static MessageParser  messageParser = new MessageParser();

    public static  MessageParser getInstance() {
        synchronized (messageParser){
            if(messageParser == null){
                synchronized (messageParser){
                    if(messageParser == null){
                        messageParser = new MessageParser();
                    }
                }
            }
            return messageParser;
        }
    }

    static {
        list.add(new ConnectionHandler());
        list.add(new DisConnectionHandler());
        list.add(new TextMessageHandler());
        list.add(new QueryMessageHandler());
    }

    public void handle(Message message, Socket socket, ServerData serverData){
        list.forEach((a)->{
            if(a.isSupport(message)){
                try {
                    a.handle(message, socket,serverData);
                }catch (Exception e){

                }
            }
        });
    }


}
