package com.example.second_book_exchange;

import java.io.Serializable;

public class BookInfoList implements Serializable {

    private String title;
    private String content;

    public BookInfoList(String title, String content) {
        this.title = title;
        this.content = content;
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
}
