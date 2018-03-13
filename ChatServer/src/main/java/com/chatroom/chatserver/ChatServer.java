package com.chatroom.chatserver;


import com.zj.data.Constants;
import com.zj.data.ServerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @Title: ChatServer.java
 * @Description: TODO 后端服务器程序
 * @author ZhangJing   https://github.com/Laity000/ChatRoom-JavaFX
 * @date 2017年5月17日 上午11:21:38
 *
 */
public class ChatServer {

	private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

	private volatile static ServerData serverData = new ServerData();

	public static void main(String[] args) throws IOException{
		ServerSocket ss = new ServerSocket(Constants.SERVER_PORT);
		try {
			while(true)
			{
				Socket socket = ss.accept();
				new Thread(new ClientSocket(socket,serverData)).start();
			}
		}catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    }
	}
}
