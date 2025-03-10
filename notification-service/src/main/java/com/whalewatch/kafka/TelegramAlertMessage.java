package com.whalewatch.kafka;

import java.io.Serializable;

public class TelegramAlertMessage implements Serializable {
    private Long chatId;
    private String message;

    public TelegramAlertMessage() { }

    public TelegramAlertMessage(Long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
