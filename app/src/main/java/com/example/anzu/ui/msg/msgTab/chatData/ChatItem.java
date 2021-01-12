package com.example.anzu.ui.msg.msgTab.chatData;

public class ChatItem {
    private int imageId;
    private String title;
    private String content;
    private String time;
    private int msgNum;

    public ChatItem(int imageId, String title, String content, String time, int msgNum) {
        this.imageId = imageId;
        this.title = title;
        this.content = content;
        this.time = time;
        this.msgNum = msgNum;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }
}
