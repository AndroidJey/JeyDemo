package com.jey.jeydemo.entity;

/**
 * Created by jey on 2017/4/19.
 */

public class UnReadMsgModel {
    private String userName;
    private String message;
    private String chatDate;

    public UnReadMsgModel() {
    }

    public UnReadMsgModel(String userName, String message, String chatDate) {
        this.userName = userName;
        this.message = message;
        this.chatDate = chatDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    @Override
    public String toString() {
        return "UnReadMsgModel{" +
                "userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", chatDate='" + chatDate + '\'' +
                '}';
    }
}
