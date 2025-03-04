package com.whalewatch.telegram;

public class TelegramMessageEvent {
    private final Long chatId;
    private final String message;

    public TelegramMessageEvent(Long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }
}

