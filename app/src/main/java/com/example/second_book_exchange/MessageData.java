package com.example.second_book_exchange;

public class MessageData {

    private String senderUid;
    private String msg;
    private long time;

    public MessageData(String senderUid, String msg, long time) {
        this.senderUid = senderUid;
        this.msg = msg;
        this.time = time;
    }

    public MessageData() {
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
