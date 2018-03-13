package com.zj.data;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerData {

    //用户名与客户端对象的映射
    private Map<String, Socket> userSocketMapping = new ConcurrentHashMap();
    //用户信息集合
    private  LinkedList<UserInfo> userList = new LinkedList();

    public Map<String, Socket> getUserSocketMapping() {
        return userSocketMapping;
    }
    public LinkedList<UserInfo> getUserList() {
        return userList;
    }
}
