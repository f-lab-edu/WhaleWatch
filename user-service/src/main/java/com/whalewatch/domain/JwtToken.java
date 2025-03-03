package com.whalewatch.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_tokens")
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private String email;
    private LocalDateTime expiry;

    protected JwtToken() {}

    public JwtToken(String token, String email, LocalDateTime expiry) {
        this.token = token;
        this.email = email;
        this.expiry = expiry;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public LocalDateTime getExpiry() { return expiry; }

    public void setId(Long id) { this.id = id; }
    public void setToken(String token) { this.token = token; }
    public void setEmail(String email) { this.email = email; }
    public void setExpiry(LocalDateTime expiry) { this.expiry = expiry; }
}
