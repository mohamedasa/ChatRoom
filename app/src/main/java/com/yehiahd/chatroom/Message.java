package com.yehiahd.chatroom;

/**
 * Created by yehia on 23/09/17.
 */

public class Message {

    private String name;
    private String msg;
    private String imgUrl;
    private String date;


    public String getName() {
        return name;
    }

    public Message setName(String name) {
        this.name = name;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Message setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Message setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Message setDate(String date) {
        this.date = date;
        return this;
    }
}
