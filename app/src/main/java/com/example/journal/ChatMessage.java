package com.example.journal;

public class ChatMessage {

    public String sender;
    public String message;
    public long timestamp;
    public boolean isUser;

    public ChatMessage() {} // Firestore needs empty constructor

    public ChatMessage(String sender, String message, long timestamp, boolean isUser) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.isUser = isUser;
    }
}
