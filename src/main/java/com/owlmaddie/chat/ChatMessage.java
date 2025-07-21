package com.owlmaddie.chat;

import com.google.gson.annotations.Expose;

/**
 * The {@code ChatMessage} class represents a single message.
 */
public class ChatMessage {
    @Expose
    public String message;
    @Expose
    public String name;
    @Expose
    public ChatDataManager.ChatSender sender;
    @Expose
    public Long timestamp;

    public ChatMessage(String message, ChatDataManager.ChatSender sender, String playerName) {
        this.message = message;
        this.sender = sender;
        this.name = playerName;
        this.timestamp = System.currentTimeMillis();
    }
}