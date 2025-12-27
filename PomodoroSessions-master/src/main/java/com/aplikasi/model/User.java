package com.aplikasi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model untuk User
 * Mewakili pengguna aplikasi Pomodoro
 */
public class User {
    private int user_id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    /**
     * Constructor lengkap (dari database)
     * Digunakan saat load user dari database
     */
    public User(int user_id, String username, String email, 
                LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
    
    /**
     * Constructor untuk registrasi user baru
     * Digunakan saat user register, sebelum save ke database
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.lastLogin = null; // Belum pernah login
    }
    
    // ==================== GETTERS ====================
    
    /**
     * PENTING: Method ini digunakan oleh semua controller
     * Jangan diganti nama menjadi getUser_id()
     */
    public int getId() {
        return user_id;
    }
    
    /**
     * Getter alternatif untuk kompatibilitas
     * Tetap sediakan untuk backward compatibility
     */
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
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Update waktu login terakhir ke sekarang
     * Dipanggil saat user berhasil login
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    /**
     * Override toString untuk debugging
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + user_id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    
    /**
     * Get formatted createdAt untuk display
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return createdAt.format(formatter);
    }
    
    /**
     * Get formatted lastLogin untuk display
     */
    public String getFormattedLastLogin() {
        if (lastLogin == null) return "Never";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return lastLogin.format(formatter);
    }
    
    /**
     * Cek apakah user pernah login
     */
    public boolean hasLoggedInBefore() {
        return lastLogin != null;
    }
    
    /**
     * Get display name (bisa dikembangkan untuk firstname + lastname)
     */
    public String getDisplayName() {
        return username;
    }
    
    /**
     * Validasi email format (basic)
     */
    public boolean isEmailValid() {
        return email != null && email.contains("@") && email.contains(".");
    }
}