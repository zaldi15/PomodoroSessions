package com.aplikasi.dao;

import com.aplikasi.model.User;
import com.aplikasi.util.DBConnection;
import com.aplikasi.util.PasswordUtil;
import java.sql.*;
import java.time.LocalDateTime;

public class UserDAO {

    private LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        return (timestamp != null) ? timestamp.toLocalDateTime() : null;
    }

    // ✅ --- REGISTRASI AMAN (hash DI DALAM DAO) ---
    public boolean registerUser(String username, String password, String email) {

        String hashedPassword = PasswordUtil.hashPassword(password);
        System.out.println("✅ REGISTER HASH = " + hashedPassword);

        String sql = "INSERT INTO users (username, password, email, created_at) VALUES (?, ?, ?, NOW())";

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

    // ✅ --- LOGIN ---
    public User loginUser(String username, String password) {
        String sql = "SELECT user_id, username, password, email, created_at, last_login FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("\n🔎 LOGIN username input = [" + username + "]");

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ USERNAME TIDAK DITEMUKAN");
                return null;
            }

            String storedHash = rs.getString("password");
            String inputHash = PasswordUtil.hashPassword(password);

            System.out.println("✅ HASH INPUT  = " + inputHash);
            System.out.println("✅ HASH STORED = " + storedHash);

            if (!inputHash.equals(storedHash)) {
                System.out.println("❌ PASSWORD TIDAK MATCH");
                return null;
            }

            System.out.println("🎉 PASSWORD MATCH — LOGIN BERHASIL");

            return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                convertTimestampToLocalDateTime(rs.getTimestamp("created_at")),
                convertTimestampToLocalDateTime(rs.getTimestamp("last_login"))
            );

        } catch (SQLException e) {
            System.err.println("DAO Error Login: " + e.getMessage());
            return null;
        }
    }

    // ✅ --- UPDATE LAST LOGIN FIX ---
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";

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
