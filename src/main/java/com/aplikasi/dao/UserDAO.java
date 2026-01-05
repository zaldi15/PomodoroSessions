package com.aplikasi.dao;

import com.aplikasi.model.User;
import com.aplikasi.util.DBConnection;
import com.aplikasi.util.PasswordUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

  
    private LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        return (timestamp != null) ? timestamp.toLocalDateTime() : null;
    }

   
    public boolean registerUser(String username, String password, String email) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        String sql = "INSERT INTO users (username, password, email, role, created_at) VALUES (?, ?, ?, 'user', NOW())";

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

    // 2. LOGIN USER
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String inputHash = PasswordUtil.hashPassword(password);

                if (inputHash.equals(storedHash)) {
                    updateLastLogin(rs.getInt("user_id"));

                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        convertTimestampToLocalDateTime(rs.getTimestamp("created_at")),
                        convertTimestampToLocalDateTime(rs.getTimestamp("last_login"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error Login: " + e.getMessage());
        }
        return null;
    }

   
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        // Mengambil semua kecuali admin sendiri bisa menjadi pilihan, 
        // tapi di sini kita ambil semua yang role-nya 'user'
        String sql = "SELECT * FROM users WHERE role = 'user' ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("DAO Error Get All Users: " + e.getMessage());
        }
        return userList;
    }

    
    public List<User> searchUsers(String keyword) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'user' AND (username LIKE ? OR email LIKE ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("DAO Error Search Users: " + e.getMessage());
        }
        return userList;
    }

    // 5. HAPUS USER
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error Delete User: " + e.getMessage());
            return false;
        }
    }

    // 6. UPDATE LAST LOGIN
    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DAO Error Update Last Login: " + e.getMessage());
        }
    }

    // 7. HITUNG TOTAL USER (Untuk statistik dashboard)
    public int getTotalUsersCount() {
        String sql = "SELECT COUNT(*) AS total FROM users WHERE role = 'user'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method agar kode tidak berulang (DRY - Don't Repeat Yourself)
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("role"),
            convertTimestampToLocalDateTime(rs.getTimestamp("created_at")),
            convertTimestampToLocalDateTime(rs.getTimestamp("last_login"))
        );
    }

}
