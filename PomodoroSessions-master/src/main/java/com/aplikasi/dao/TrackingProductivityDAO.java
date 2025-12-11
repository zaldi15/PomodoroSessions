package com.aplikasi.dao;

import com.aplikasi.model.TrackingProductivity;
import com.aplikasi.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrackingProductivityDAO {

    // Mengambil data produktivitas berdasarkan User ID untuk ditampilkan di Grafik/Laporan
    public TrackingProductivity getTrackingStats(int userId) {
        TrackingProductivity stats = null;
        String query = "SELECT * FROM tracking_productivity WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats = new TrackingProductivity();
                    stats.setTrackingId(rs.getInt("tracking_id"));
                    stats.setUserId(rs.getInt("user_id"));
                    stats.setTotalSessions(rs.getInt("total_sessions"));
                    stats.setTotalFocusHours(rs.getDouble("total_focus_hours"));
                    stats.setLastUpdated(rs.getTimestamp("last_updated"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Method untuk inisialisasi data (dipanggil saat User baru Register)
    public void initTrackingUser(int userId) {
        String query = "INSERT INTO tracking_productivity (user_id, total_sessions, total_focus_hours, last_updated) VALUES (?, 0, 0.0, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // LOGIKA UTAMA: Update sesi setelah timer selesai
    // Method ini akan menambah total_sessions + 1 dan mengakumulasi jam fokus
    public boolean recordSession(int userId, double sessionDurationHours) {
        // Cek dulu apakah user sudah punya row di tabel tracking
        if (getTrackingStats(userId) == null) {
            initTrackingUser(userId);
        }

        String query = "UPDATE tracking_productivity SET " +
                "total_sessions = total_sessions + 1, " +
                "total_focus_hours = total_focus_hours + ?, " +
                "last_updated = NOW() " +
                "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, sessionDurationHours);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}