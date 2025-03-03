package com.whalewatch.dto;

import java.io.Serializable;

public class UserRegistrationEventDto implements Serializable {
    private Long chatId;
    private String email;
    private String username;

    public UserRegistrationEventDto() { }

    public UserRegistrationEventDto(Long chatId, String email, String username) {
        this.chatId = chatId;
        this.email = email;
        this.username = username;
    }

    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
