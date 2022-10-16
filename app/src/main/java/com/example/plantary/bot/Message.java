package com.example.plantary.bot;

public class Message {

    private final String textMessage;
    private final boolean isBot;

    public Message(String textMessage, boolean isBot) {
        this.textMessage = textMessage;
        this.isBot = isBot;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public boolean isBot() {
        return isBot;
    }
}
