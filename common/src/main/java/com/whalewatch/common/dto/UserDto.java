package com.whalewatch.common.dto;

public class UserDto {
    private String email;
    private String username;

    public UserDto() {}

    public UserDto(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
