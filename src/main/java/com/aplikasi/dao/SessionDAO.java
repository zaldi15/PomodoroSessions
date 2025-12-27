package com.aplikasi.dao;

import com.aplikasi.model.PomodoroSession;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class SessionDAO {

    // =============================================================
    // 1. CREATE: Simpan Sesi Baru
    // =============================================================
    public static boolean insertSession(PomodoroSession session) {
        String sql = """
            INSERT INTO pomodoro_sessions 
            (user_id, start_time, end_time, focus_duration, break_duration, total_sessions, completed, category)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, session.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(session.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(session.getEndTime()));
            stmt.setInt(4, session.getFocusDuration());
            stmt.setInt(5, session.getBreakDuration());
            stmt.setInt(6, session.getTotalSessions());
            stmt.setBoolean(7, session.isCompleted());
            stmt.setString(8, (session.getCategory() == null) ? "Lainnya" : session.getCategory());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =============================================================
    // 2. READ: Ambil Riwayat Sesi
    // =============================================================
    
    // Ambil semua sesi milik satu user
    public static List<PomodoroSession> getSessionsByUser(int userId) {
        List<PomodoroSession> list = new ArrayList<>();
        String sql = "SELECT * FROM pomodoro_sessions WHERE user_id = ? ORDER BY start_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Ambil beberapa sesi terakhir (untuk Dashboard)
    public static List<PomodoroSession> getRecentSessions(int userId, int limit) {
        List<PomodoroSession> list = new ArrayList<>();
        String sql = "SELECT * FROM pomodoro_sessions WHERE user_id = ? ORDER BY start_time DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // =============================================================
    // 3. ANALYTICS: Statistik untuk Chart
    // =============================================================

    // Global Statistik per Kategori (Untuk Admin PieChart)
    public static Map<String, Double> getGlobalCategoryStats() {
        Map<String, Double> stats = new HashMap<>();
        String sql = "SELECT category, SUM(focus_duration) AS total FROM pomodoro_sessions WHERE completed = 1 GROUP BY category";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String cat = rs.getString("category");
                stats.put(cat == null ? "Lainnya" : cat, rs.getDouble("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // Statistik Per User (Untuk User PieChart)
    public static Map<String, Double> getUserCategoryStats(int userId) {
        Map<String, Double> stats = new HashMap<>();
        String sql = "SELECT category, SUM(focus_duration) AS total FROM pomodoro_sessions WHERE user_id = ? AND completed = 1 GROUP BY category";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String cat = rs.getString("category");
                stats.put(cat == null ? "Lainnya" : cat, rs.getDouble("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // Tren Mingguan: Total menit fokus per hari selama 7 hari terakhir
    public static Map<LocalDate, Integer> getWeeklyFocusStats(int userId) {
        Map<LocalDate, Integer> weeklyData = new LinkedHashMap<>();
        String sql = """
            SELECT DATE(start_time) as tgl, SUM(focus_duration) as total 
            FROM pomodoro_sessions 
            WHERE user_id = ? AND start_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
            GROUP BY DATE(start_time)
            ORDER BY tgl ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                weeklyData.put(rs.getDate("tgl").toLocalDate(), rs.getInt("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return weeklyData;
    }

    // =============================================================
    // 4. DELETE: Hapus Data
    // =============================================================

    public static boolean deleteSession(int sessionId) {
        String sql = "DELETE FROM pomodoro_sessions WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Hapus semua sesi user (Dipanggil saat Admin menghapus/ban user)
    public static boolean deleteAllSessionsByUser(int userId) {
        String sql = "DELETE FROM pomodoro_sessions WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() >= 0; // 0 berarti tidak ada sesi, tetap sukses
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =============================================================
    // HELPER METHODS
    // =============================================================

    private static PomodoroSession mapResultSetToSession(ResultSet rs) throws SQLException {
        return new PomodoroSession(
            rs.getInt("session_id"),
            rs.getInt("user_id"),
            rs.getTimestamp("start_time").toLocalDateTime(),
            rs.getTimestamp("end_time").toLocalDateTime(),
            rs.getInt("focus_duration"),
            rs.getInt("break_duration"),
            rs.getInt("total_sessions"),
            rs.getBoolean("completed"),
            rs.getString("category")
        );
    }
    
    public static int getTotalFocusMinutes(int userId) {
        String sql = "SELECT SUM(focus_duration) AS total FROM pomodoro_sessions WHERE user_id = ? AND completed = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}