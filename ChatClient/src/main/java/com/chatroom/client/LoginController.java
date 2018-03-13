package com.chatroom.client;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chatroom.stage.ControlledStage;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 *
 * @Title: LoginController.java
 * @Description: TODO 登录窗口控制器
 * @author ZhangJing   https://github.com/Laity000/ChatRoom-JavaFX
 * @date 2017年5月17日 上午11:19:25
 *
 */
public class LoginController extends ControlledStage implements  Initializable{
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	//Stage管理器
	//private StageController myController;
	//loginController对象
	private static LoginController instance;

	@FXML private BorderPane borderPane;
	@FXML private ImageView defaultImgView;
    @FXML private ImageView randomImgView;
	@FXML private TextField hostnameTextfield;
    @FXML private TextField portTextfield;
    @FXML private TextField usernameTextfield;
    @FXML private Text resultText;

    private double xOffset;
    private double yOffset;

    public final static String[] NameList =
    	{"Alex", "Brenda", "Connie", "Donny",
         "Lynne", "Myrtle", "Rose", "Tony",
         "Williams", "Zach"};


    //注意这里不是单例的形式，UI的Controller相当于回调函数的管理集合。
    //不能用private单例调用，应该由系统调用。这里是为了获得LoginController对象。
    public LoginController() {
    	instance = this;
	}
    /**
     * 获得UI的Controller对象
     * @return
     */
    public static LoginController getInstance(){
    	return instance;
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
	 * 随机按钮事件：用于随意产生用户名和头像
	 * @param event
	 */
	@FXML
	public void randomBtnAction(ActionEvent event){
		//清空结果提示
		resultText.setText("");
		int num = new Random().nextInt(10);
		usernameTextfield.setText(NameList[num]);
        defaultImgView.setVisible(false);
        randomImgView.setVisible(true);
        Image image = new Image("images/" + NameList[num] + ".png");
        randomImgView.setImage(image);
	}

	@FXML
	public void connectBtnAction(ActionEvent event){
		String username = usernameTextfield.getText().trim();
		String hostname = hostnameTextfield.getText().trim();
		int port = Integer.parseInt(portTextfield.getText().trim());
		//判断用户名不为空
		if("".equals(username)){
			resultText.setText("Username cannot be empty !");
			return;
		}
		//对头像进行判断
		boolean isDefaultPic = true;
		for(int i= 0; i<10; i++){
			if(username.compareTo(NameList[i]) == 0){
				isDefaultPic = false;
				break;
			}
		}
		String userpic = (isDefaultPic == true) ? "Default.png" : username + ".png";
		ClientThread clientThread = new ClientThread(hostname, port, username, userpic);
		new Thread(clientThread).start();


	}

	/**
	 * 最小化窗口
	 * @param event
	 */
	@FXML public void minBtnAction(ActionEvent event){
		getLocalStage().setIconified(true);

	}
	/**
	 * 关闭窗口，关闭程序
	 * @param event
	 */
	@FXML public void closeBtnAction(ActionEvent event){
		Platform.exit();
        System.exit(0);

	}

	public void setResultText(String content){
		this.resultText.setText(content);
	}


}
