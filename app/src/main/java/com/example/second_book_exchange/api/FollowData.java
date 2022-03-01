package com.example.second_book_exchange.api;

public class FollowData {

    private String myUid;
    private String targetUid;

    public FollowData(String myUid, String targetUid) {
        this.myUid = myUid;
        this.targetUid = targetUid;
    }

    public FollowData() {
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public String getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }
}
