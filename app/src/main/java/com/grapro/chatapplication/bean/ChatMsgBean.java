package com.grapro.chatapplication.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.grapro.chatapplication.content.MsgType;

public class ChatMsgBean implements MultiItemEntity {
    private String name;
    private String id;
    private String msg;
    private String base64Image;
    private int msgType;
    private String time;
    //item类型：发送 接收
    private int itemType;
    //消息状态：成功 发送中 失败
    private int msgStatus;
    private String picPath = "";
    private boolean destoryd = false;

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public boolean isDestoryd() {
        return destoryd;
    }

    public void setDestoryd(boolean destoryd) {
        this.destoryd = destoryd;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ChatMsgBean() {
    }

    public ChatMsgBean(String name, String id, String msg, int msgType, String time, int itemType, int msgStatus,String picPath,boolean destoryd) {
        this.name = name;
        this.id = id;
        this.msgType = msgType;
        this.picPath = picPath;
        this.msg = msg;
        this.time = time;
        this.itemType = itemType;
        this.msgStatus = msgStatus;
        this.destoryd=destoryd;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

}
