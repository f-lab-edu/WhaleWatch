package com.whalewatch.common.dto;

public class UserDto {
    private int id;
    private String email;
    private String username;

    public UserDto() {}

    public UserDto(int id,String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public int getId() {
        return id;
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
