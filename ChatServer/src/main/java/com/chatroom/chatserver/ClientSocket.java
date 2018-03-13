package com.chatroom.chatserver;

import com.alibaba.fastjson.JSONObject;
import com.chatroom.chatserver.parser.MessageParser;
import com.zj.messages.Message;
import com.zj.data.ServerData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSocket implements Runnable {

    private Socket socket;
    private BufferedReader br;
    MessageParser messageParser = MessageParser.getInstance();
    private ServerData serverData;

    public ClientSocket(Socket socket,ServerData serverData) throws IOException{
        this.socket = socket;
        this.serverData = serverData;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        if(socket.isClosed()){
            return ;
        }
        try {
            while (socket.isConnected()) {
                String reciveMsg = br.readLine();
                if(reciveMsg != null && !"".equals(reciveMsg.trim())){
                    Message message = JSONObject.parseObject(reciveMsg, Message.class);
                    messageParser.handle(message,socket,serverData);
                }
            }
        }catch (Exception e){

        }
    }
}
