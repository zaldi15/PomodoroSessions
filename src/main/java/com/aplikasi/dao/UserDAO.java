package com.aplikasi.dao;

import com.aplikasi.model.User;
import com.aplikasi.util.DBConnection;
import com.aplikasi.util.PasswordUtil;
import java.sql.*;
import java.time.LocalDateTime;

public class UserDAO {
    
    // --- UTILITY ---
    private LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        return (timestamp != null) ? timestamp.toLocalDateTime() : null;
    }

    // --- REGISTRASI ---
    public boolean registerUser(String username, String password, String email) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        // last_login otomatis NULL saat registrasi
        String sql = "INSERT INTO USERS (username, password, email, created_at) VALUES (?, ?, ?, NOW())";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO Error Registrasi: " + e.getMessage());
            return false;
        }
    }

    // --- LOGIN ---
    public User loginUser(String username, String password) {
        String sql = "SELECT user_id, password, email, created_at, last_login FROM USERS WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    
                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        // Login berhasil, buat objek User lengkap
                        return new User(
                            rs.getInt("user_id"),
                            username,
                            rs.getString("email"),
                            convertTimestampToLocalDateTime(rs.getTimestamp("created_at")),
                            convertTimestampToLocalDateTime(rs.getTimestamp("last_login"))
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error Login: " + e.getMessage());
        }
        return null; // Login gagal
    }
    
    // --- PERBAIKAN last_login ---
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE USERS SET last_login = NOW() WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO Error Update Last Login: " + e.getMessage());
            return false;
        }
    }
}