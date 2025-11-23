package com.aplikasi.model;

import java.time.LocalDateTime;

public class User {
    private int user_id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User(int user_id, String username, String email, LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    
}

