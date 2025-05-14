package com.grapro.chatapplication.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ChatListBean implements MultiItemEntity {
    private String name;
    private String friendId;
    private String msg;
    private int msgType;
    private String time;
    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ChatListBean() {
    }

    public ChatListBean(String name, String id, String msg, int msgType, String time) {
        this.name = name;
        this.friendId = id;
        this.msg = msg;
        this.msgType = msgType;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
