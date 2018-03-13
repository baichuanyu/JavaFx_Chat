package com.chatroom.client;

import com.alibaba.fastjson.JSONObject;
import com.zj.data.UserInfo;
import com.zj.enums.MessageTypeEnums;
import com.zj.messages.ConnectMessage;
import com.zj.messages.Message;
import com.zj.messages.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 *
 * @Title: ClientThread.java
 * @Description: TODO 通讯线程
 * @author ZhangJing   https://github.com/Laity000/ChatRoom-JavaFX
 * @date 2017年5月17日 上午11:18:37
 *
 */
public class ClientThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClientThread.class);
	//各UI控制器对象
	private LoginController loginController = LoginController.getInstance();
	private ChatController chatController = ChatController.getInstance();
	//主机地址
	private String hostName;
	//端口号
	private int port;
	//套接字
	private Socket socket;
	private UserInfo userInfo;
	// 该线程所处理的Socket所对应的输入流
	private BufferedReader br = null;
	// 该线程所处理的Socket所对应的输出流
	private PrintStream ps = null;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * 设备连接构造函数
	 */
	public ClientThread(String hostname, int port, String username, String userpic){
		this.hostName = hostname;
		this.port = port;
		userInfo = new UserInfo(username,userpic);
		chatController.setClientThread(this);
	}

	public void run() {
		try{
			socket = new Socket();
			SocketAddress endpoint = new InetSocketAddress(hostName, port);
			//设置连接超时时间
			socket.connect(endpoint, 5 * 1000);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			ps = new PrintStream(socket.getOutputStream());
			loginConnect();
			while (socket.isConnected()) {
				//读取来自客户端的消息
				String revString = br.readLine();
				if (revString != null) {
					Message message = JSONObject.parseObject(revString, Message.class);
					//TODO:对服务器的消息进行解析
					switch (message.getType()) {
						case LOGIN:
							ConnectMessage connectMessage = (ConnectMessage) message;
							if(connectMessage.isSucc()) {
								loginController.changeStage(ChatClient.ChatUIID);
							}else {
								loginController.setResultText(connectMessage.getMsg());
							}
							break;
						case LOGOUT:
							destroy();
							break;
						case TEXTMSG:
							TextMessage textMessage = (TextMessage)message;
							chatController.addOtherMessges(textMessage);
							break;
						case USERLIST:
							chatController.setUserList(message.getUserlist());
							break;

						case NOTIFICATION:
							TextMessage textMessage1 = (TextMessage)message;
							chatController.addNotification(textMessage1.getContent());
							break;
						default:
							break;
					}

				}

			}

		}
		catch(SocketTimeoutException ex)
		{
			ex.printStackTrace();
			logger.info("{} 连接超时！", socket);
			loginController.setResultText(socket +"连接超时！");
		}
		catch (Exception e)
		{
			e.printStackTrace();

			logger.info("{} 连接错误！", socket);
			loginController.setResultText(socket +"连接错误！");

		}
	}


	/**
	 * 用户连接请求类型消息
	 * @throws IOException
	 */
	public void loginConnect() {
		//创建个人信息
		ConnectMessage message = new ConnectMessage();
		message.setType(MessageTypeEnums.LOGIN);
		message.setUserInfo(userInfo);
		String messagesString = JSONObject.toJSONString(message);
		ps.println(messagesString);
	}

	/**
	 * 用户注销请求类型消息
	 * @throws IOException
	 */
	public void disconnect() {
		ConnectMessage message = new ConnectMessage();
		message.setType(MessageTypeEnums.LOGOUT);
		message.setUserInfo(userInfo);
		ps.println(JSONObject.toJSONString(message));
	}

	/**
	 * 断开物理连接
	 * @throws IOException
	 */
	public void destroy() {
		logger.debug("正在断开物理连接..");
		new Thread(){
			@Override
			public void run() {
				try {
					if(br != null){
						br.close();
						br = null;
					}
					if(ps != null){
						ps.close();
						ps = null;
					}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		}.start();
		logger.debug("物理连接断开成功！");
	}
	/**
	 * 对话类型消息
	 * @param to
	 * @param content
	 * @throws IOException
	 */
	public void sendMsg(String to, String content) {
		//创建Msg消息
		TextMessage message = new TextMessage();
		message.setType(MessageTypeEnums.TEXTMSG);
		message.setFrom(this.userInfo.getUsername());
		message.setTo(to);
		message.setContent(content);
		ps.println(JSONObject.toJSONString(message));
	}


}
