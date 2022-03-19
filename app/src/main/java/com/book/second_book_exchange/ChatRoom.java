package com.book.second_book_exchange;

public class ChatRoom {

    private String chatRoomId;

    public ChatRoom(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public ChatRoom() {
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
