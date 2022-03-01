package com.example.second_book_exchange;

public class PublicMessageData {

    private long id;   //後台產生
    private String uidForLeftMsg;
    private String msg;
    private String bookName;
    private String uploaderUid;
    private long bookId;

    public PublicMessageData(long id, String uidForLeftMsg, String msg, String bookName, String uploaderUid, long bookId) {
        this.id = id;
        this.uidForLeftMsg = uidForLeftMsg;
        this.msg = msg;
        this.bookName = bookName;
        this.uploaderUid = uploaderUid;
        this.bookId = bookId;
    }

    public PublicMessageData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUidForLeftMsg() {
        return uidForLeftMsg;
    }

    public void setUidForLeftMsg(String uidForLeftMsg) {
        this.uidForLeftMsg = uidForLeftMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getUploaderUid() {
        return uploaderUid;
    }

    public void setUploaderUid(String uploaderUid) {
        this.uploaderUid = uploaderUid;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
