package com.whalewatch.redis;

import java.io.Serializable;

public class RegistrationData implements Serializable {
    private String email;
    private String username;

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
