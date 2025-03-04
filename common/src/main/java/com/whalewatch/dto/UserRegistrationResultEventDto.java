package com.whalewatch.dto;

import java.io.Serializable;

public class UserRegistrationResultEventDto implements Serializable {
    private Long chatId;
    private boolean success;
    private String message;

    public UserRegistrationResultEventDto() { }

    public UserRegistrationResultEventDto(Long chatId, boolean success, String message) {
        this.chatId = chatId;
        this.success = success;
        this.message = message;
    }

    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
