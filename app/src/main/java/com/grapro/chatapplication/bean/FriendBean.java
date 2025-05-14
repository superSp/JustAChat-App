package com.grapro.chatapplication.bean;

/**
 * Copyright 2023 json.cn
 */
import java.util.List;

/**
 * Auto-generated: 2023-02-17 15:30:29
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class FriendBean {

    private int code;
    private String msg;
    private List<Data> data;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }
    public class Data {

        private int user_id;
        private String user_name;
        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
        public int getUser_id() {
            return user_id;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
        public String getUser_name() {
            return user_name;
        }


    }
}
