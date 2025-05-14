/**
 * Copyright 2023 json.cn
 */
package com.grapro.chatapplication.bean;

/**
 * Auto-generated: 2023-02-15 18:4:37
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class UserBean {

    private int code;
    private String msg;
    private Data data;
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

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }
/**
 * Copyright 2023 json.cn
 */

    public static class Data {

        private int user_id;
        private String user_name;
        private String user_pwd;
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

        public void setUser_pwd(String user_pwd) {
            this.user_pwd = user_pwd;
        }
        public String getUser_pwd() {
            return user_pwd;
        }

    }
}