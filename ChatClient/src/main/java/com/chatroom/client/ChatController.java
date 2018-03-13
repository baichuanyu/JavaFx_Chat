package com.chatroom.client;

import com.chatroom.bubble.BubbleSpec;
import com.chatroom.bubble.BubbledTextFlow;
import com.chatroom.emojis.EmojiDisplayer;
import com.chatroom.stage.ControlledStage;
import com.zj.data.UserInfo;
import com.zj.messages.Message;
import com.zj.messages.TextMessage;
import com.zj.util.Utils;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
/**
 *
 * @Title: ChatController.java
 * @Description: TODO 聊天室主窗口控制器
 * @author ZhangJing   https://github.com/Laity000/ChatRoom-JavaFX
 * @date 2017年5月17日 上午11:17:35
 *
 */
public class ChatController extends ControlledStage implements Initializable{
	//ChatController对象
	private static ChatController instance;
	//ClientThread连接线程对象
	ClientThread clientThread;
	//界面根容器
	@FXML private BorderPane borderPane;
	//用户头像
	@FXML private ImageView userImageView;
	//用户名
	@FXML private Label usernameLabel;
	//在线用户列表
	@FXML private ListView<UserInfo> userListView;
	//在线用户人数
	@FXML private Label userCountLabel;
	//消息显示列表
	@FXML private ListView<HBox> chatPaneListView;
	//消息发送框
	@FXML private TextArea messageBoxTextArea;
	//对话消息按钮
	@FXML private Button sendButton;
	//聊天对象显示文本框
	@FXML private Label otherUserNameLabel;
    private double xOffset;
    private double yOffset;
	//用户信息列表
	Queue<UserInfo> userInfoList = new LinkedList<>();
	//被选中的用户名
	String otherUserName = Utils.ALL;
	 //注意这里不是单例的形式，UI的Controller相当于回调函数的管理集合。
    //不能用private单例调用，应该由系统调用。这里是为了获得LoginController对象。
    public ChatController() {
    	instance = this;
	}
    /**
     * 获得UI的Controller的对象
     * @return
     */
    public static ChatController getInstance(){
    	return instance;
    }

    public TextArea getMessageBoxTextArea() {
		return messageBoxTextArea;
	}

    public void setClientThread(ClientThread clientThread) {
		this.clientThread = clientThread;
		if (clientThread != null){
			init();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		/* Drag and Drop */
        borderPane.setOnMousePressed(event -> {
            xOffset = getLocalStage().getX() - event.getScreenX();
            yOffset = getLocalStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });
        borderPane.setOnMouseDragged(event -> {
        	getLocalStage().setX(event.getScreenX() + xOffset);
        	getLocalStage().setY(event.getScreenY() + yOffset);
        });
        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });
        //设置图标
        setIcon("images/icon_chatroom.png");
	}

	/**
	 * 发送消息事件
	 * @throws IOException
	 */
	@FXML
	public void sendBtnAction() {
		String content = messageBoxTextArea.getText();
		if(!content.isEmpty()){
			clientThread.sendMsg(otherUserName, content);
			messageBoxTextArea.clear();
			showSelfMessage(content);
		}
	}
	/**
	 * 设置发送快捷键
	 * @param event
	 * @throws IOException
	 */
	@FXML public void sendMethod(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendBtnAction();
        }
    }

	/**
	 * 关闭界面
	 * @throws IOException
	 */
	@FXML public void closeImgViewPressedAction() {
		//首先需要向服务器注销用户
		clientThread.disconnect();
		clientThread.destroy();
		//退出系统
        Platform.exit();
        System.exit(0);
    }

	/**
	 * 注销用户
	 * @throws IOException
	 */
	@FXML public void logoutImgViewPressedAction() {
		//首先需要向服务器注销用户
		clientThread.disconnect();
		clientThread.destroy();
		//然后清除消息对话框
		chatPaneListView.getItems().clear();
		//切换到登录界面
		changeStage(ChatClient.LoginUIID);
		//最后才能卸载该stage，否则切换需要的资源。该思路不适合，只需要两个固定不需要卸载的界面。
		//myController.unloadStage(myStageUIID);

    }
	/**
	 * emoji选择器启动按钮事件
	 * @param event
	 */
	@FXML public void emojiSelectorBtnAction(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		java.io.File file = fileChooser.showOpenDialog(null);
		String content = messageBoxTextArea.getText();
		if(file!=null){
			Message ms = new Message();
			ms.setTo("aa");
			ms.setFrom("vv");


		}
//			//设置emoji选择器的位置
//			double prex = getLocalStage().getX();
//			double prey = getLocalStage().getY();
//			double preheight = getLocalStage().getHeight();
//			double prewidth = getLocalStage().getWidth();
//			setStagePos(ChatClient.EmojiSelectorUIID, prex + prewidth/2, prey + preheight/2);
//			//打开
			openStage(ChatClient.EmojiSelectorUIID);
//		}else {
//			//关闭
//			closeStage(ChatClient.EmojiSelectorUIID);
//		}
	}

	/**
	 * 初始化Chat界面，更新如用户头像、用户名
	 */
	private void init(){
		userImageView.setImage(new Image("images/" + this.clientThread.getUserInfo().getUserpic()));
		usernameLabel.setText(this.clientThread.getUserInfo().getUsername());
	}

	/**
	 * 设置在线用户列表，并显示在线人数(需要在列表中排除本机用户)
	 * @param userInfolist 用户集
	 */
	public void setUserList(LinkedList<UserInfo> userInfolist) {

		this.userInfoList = userInfolist;
		/*
		System.out.print("UserList:");
		for(UserInfo user: userInfolist){
			System.out.println(user.getUsername());
		}
		*/

		//为listview数据源准备ALL所有人选项
		if(!userInfolist.getFirst().equals(Utils.ALL)){
			UserInfo allUser = new UserInfo(Utils.ALL,"All.png");
			userInfolist.addFirst(allUser);
		}
		//在线用户数量
		int userCount = userInfolist.size()-1;

		//本机用户不需要显示
		for(UserInfo user : userInfolist){
			if(user.getUsername().equals(this.clientThread.getUserInfo().getUsername())){
				userInfolist.remove(user);
				break;//注意缺少break会出bug，因为遍历中ArrayList不可变。使用Iterator的方式也可以顺利删除和遍历。
			}
		}
		//设置在线用户列表
		Platform.runLater(() -> {
			 //数据源
			 ObservableList<UserInfo> users = FXCollections.observableList(userInfolist);
			 userListView.setItems(users);
			 //自定义ListView
			 userListView.setCellFactory((ListView<UserInfo> L) -> new UsersCell());

			 //设置在线用户人数
			 userCountLabel.setText(userCount + "");
		 });

		/**
		 * userListView列表项点击事件监视器
		 */
		userListView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends UserInfo> ov, UserInfo old_val,
						UserInfo new_val) -> {
							otherUserName = new_val.getUsername();
							if(otherUserName.equals(Utils.ALL)){
								otherUserNameLabel.setText("Chat with everyone..");
							}else {
								//System.out.println(new_val.getUsername());
								otherUserNameLabel.setText("Chat with " + otherUserName +":");
							}
						});

	}


	/**
	 * 静态内部类：自定义ListVIew表项
	 * @author PC
	 *
	 */
	static class UsersCell extends ListCell<UserInfo>{

		@Override
		protected void updateItem(UserInfo item, boolean empty) {
			// TODO Auto-generated method stub
			super.updateItem(item, empty);
			setGraphic(null);
            setText(null);
            if (item != null) {

                HBox hBox = new HBox();
                hBox.setSpacing(5); //节点之间的间距

	            Text name = new Text(item.getUsername());
	            name.setFont(new Font("Arial", 20));//字体样式和大小
	            ImageView statusImageView = new ImageView();
	            Image statusImage = new Image("images/online.png", 16, 16,true,true);
	            statusImageView.setImage(statusImage);

	            if(item.getUsername().equals(Utils.ALL)){

	            	name.setText("group chat >");
	            	statusImageView.setImage(null);
	            	//hBox.setStyle("-fx-background-color: #336699;"); //背景色

	            }
	            ImageView pictureImageView = new ImageView();
	            Image image = new Image("images/" + item.getUserpic(),50,50,true,true);
	            pictureImageView.setImage(image);

	            hBox.getChildren().addAll(statusImageView, pictureImageView, name);
	            //hBox.getChildren().addAll(pictureImageView, name);
	            hBox.setAlignment(Pos.CENTER_LEFT);
	            setGraphic(hBox);
            }
		}

	}

	/**
	 * 将其他用户对话信息添加到对话列表中
	 * @param message
	 */
	public void addOtherMessges(TextMessage message){
		Task<HBox> msgHander = new Task<HBox>() {

			@Override
			protected HBox call() throws Exception {
			//寻找用户的头像名
			 String otherUserName = message.getFrom();
			 String otherUserPic = null;
			 for(UserInfo user : userInfoList){
				 if(otherUserName.equals(user.getUsername())){
					 otherUserPic = user.getUserpic();
				 }
			 }
			Image image = new Image("images/" + otherUserPic);
			ImageView profileImage = new ImageView(image);
			profileImage.setFitHeight(32);
			profileImage.setFitWidth(32);

			String content = null;
			if(message.getTo().equals(Utils.ALL)){
				content = message.getFrom() + ": " + message.getContent();
			}else {
				content = "[private message] " + message.getFrom() + ": " + message.getContent();
			}
			BubbledTextFlow otherBubbled = new BubbledTextFlow(EmojiDisplayer.createEmojiAndTextNode(content));
			otherBubbled.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

			HBox x = new HBox();
			otherBubbled.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
			x.getChildren().addAll(profileImage, otherBubbled);
			return x;
			}
		};
		msgHander.setOnSucceeded(event -> {
			chatPaneListView.getItems().add(msgHander.getValue());
		});
		Thread t2 = new Thread(msgHander);
        t2.setDaemon(true);
        t2.start();
	}
	/**
	 * 将自己的对话添加到对话列表中
	 * @param content
	 */
	public void showSelfMessage(String content){
		Platform.runLater(() ->{
			//寻找用户的头像名
			Image image = new Image("images/" + this.clientThread.getUserInfo().getUserpic());
            ImageView profileImage = new ImageView(image);
            profileImage.setFitHeight(32);
            profileImage.setFitWidth(32);
            BubbledTextFlow yourBubbled = new BubbledTextFlow(EmojiDisplayer.createEmojiAndTextNode(content));
            yourBubbled.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                    null, null)));
            HBox x = new HBox();
            x.setMaxWidth(chatPaneListView.getWidth() - 20);
            x.setAlignment(Pos.TOP_RIGHT);
            yourBubbled.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
            x.getChildren().addAll(yourBubbled, profileImage);
            chatPaneListView.getItems().add(x);
		});
	}
	/**
	 * 处理通知信息的显示
	 * @param notice
	 */
	public void addNotification(String notice){
		Platform.runLater(() ->{
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
			String timer = df.format(new Date());// new Date()为获取当前系统时间
			String content = timer +  ": " + notice;
			BubbledTextFlow noticeBubbled = new BubbledTextFlow(EmojiDisplayer.createEmojiAndTextNode(content));
			//noticeBubbled.setTextFill(Color.web("#031c30"));
			noticeBubbled.setBackground(new Background(new BackgroundFill(Color.WHITE,
                    null, null)));
            HBox x = new HBox();
            //x.setMaxWidth(chatPaneListView.getWidth() - 20);
            x.setAlignment(Pos.TOP_CENTER);
            noticeBubbled.setBubbleSpec(BubbleSpec.FACE_TOP);
            x.getChildren().addAll(noticeBubbled);
            chatPaneListView.getItems().add(x);
		});
	}


	/**
	 * 处理通知信息的显示
	 * @param notice
	 */
	public void reciewFile(String notice){
		Platform.runLater(() ->{
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
			String timer = df.format(new Date());// new Date()为获取当前系统时间
			String content = timer +  ": " + notice;
			BubbledTextFlow noticeBubbled = new BubbledTextFlow(EmojiDisplayer.createEmojiAndTextNode(content));
			//noticeBubbled.setTextFill(Color.web("#031c30"));
			noticeBubbled.setBackground(new Background(new BackgroundFill(Color.WHITE,
					null, null)));
			HBox x = new HBox();
			//x.setMaxWidth(chatPaneListView.getWidth() - 20);
			x.setAlignment(Pos.TOP_CENTER);
			noticeBubbled.setBubbleSpec(BubbleSpec.FACE_TOP);
			x.getChildren().addAll(noticeBubbled);
			chatPaneListView.getItems().add(x);
		});
	}
}
