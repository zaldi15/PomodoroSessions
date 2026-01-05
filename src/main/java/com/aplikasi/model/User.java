package com.aplikasi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class User {
    private int user_id;
    private String username;
    private String email;
    private String role; // Atribut baru untuk membedakan Admin dan User
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    
    public User(int user_id, String username, String email, String role, 
                LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
    
   
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.role = "user"; // Default role saat mendaftar
        this.createdAt = LocalDateTime.now();
        this.lastLogin = null;
    }
    
    // ==================== GETTERS ====================
    
    public int getId() {
        return user_id;
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

    public String getRole() {
        return role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    // ==================== SETTERS ====================
    
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    // ==================== UTILITY METHODS ====================

    /**
     * Mengecek apakah user ini adalah Admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + user_id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return createdAt.format(formatter);
    }
    
    public String getFormattedLastLogin() {
        if (lastLogin == null) return "Never";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return lastLogin.format(formatter);
    }

}
