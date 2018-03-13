package com.chatroom.chatserver.parser.impl;

import com.zj.messages.Message;
import com.zj.enums.MessageTypeEnums;
import com.zj.data.ServerData;

import java.io.*;
import java.net.Socket;

public class FileMessageHandler extends AbstractMessageHandler {

    @Override
    public boolean isSupport(Message message) {
        if(message.getType().equals(MessageTypeEnums.FILE)){
            return true;
        }
        return false;
    }

    @Override
    public void handle(Message message, Socket socket, ServerData serverData) throws Exception{
        String toUser = message.getTo();
        if(serverData.getUserSocketMapping().get(toUser) != null){
            Socket socket1 = serverData.getUserSocketMapping().get( message.getTo());
            BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            FileReader fr = new FileReader(new File(br.readLine()));
            PrintWriter pw = new PrintWriter(socket1.getOutputStream());
            String line = null;
            while((line=br.readLine())!=null){
                pw.println(line);
            }
            pw.close();
            fr.close();
            br.close();
            //send(message,socket1);
        }else {
            sendNotification(false, "The selected user does not exist!",socket,serverData);
        }
    }
}
