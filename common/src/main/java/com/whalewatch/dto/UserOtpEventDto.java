package com.whalewatch.dto;

import java.io.Serializable;

public class UserOtpEventDto implements Serializable {
    private Long chatId;
    private String message;

    public UserOtpEventDto() {
    }

    public UserOtpEventDto(Long chatId, String message) {
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
