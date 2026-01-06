package com.aplikasi.dao;

import com.aplikasi.model.Leaderboard;
import com.aplikasi.model.TrackingProductivity;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TrackingDAO - VERSI A
 * 
 * REQUIREMENT: Database harus punya struktur:
 * - PRIMARY KEY (tracking_id) AUTO_INCREMENT
 * - UNIQUE KEY (user_id, task_id)
 * 
 * SQL untuk fix database:
 * 
 * ALTER TABLE tracking_productivity DROP PRIMARY KEY;
 * ALTER TABLE tracking_productivity MODIFY tracking_id INT AUTO_INCREMENT PRIMARY KEY;
 * ALTER TABLE tracking_productivity ADD UNIQUE KEY unique_user_task (user_id, task_id);
 */
public class TrackingDAO {

   public static void updateSession(
        int userId,
        int taskId,
        int sessionInc,
        double hoursInc) throws SQLException {

    String checkSql = """
        SELECT tracking_id
        FROM tracking_productivity
        WHERE user_id = ? AND task_id = ?
    """;

    String insertSql = """
        INSERT INTO tracking_productivity
        (user_id, task_id, total_sessions, total_focus_hours, last_updated)
        VALUES (?, ?, ?, ?, NOW())
    """;

    String updateSql = """
        UPDATE tracking_productivity
        SET total_sessions = total_sessions + ?,
            total_focus_hours = total_focus_hours + ?,
            last_updated = NOW()
        WHERE user_id = ? AND task_id = ?
    """;

    try (Connection conn = DBConnection.getConnection()) {

        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, userId);
            checkPs.setInt(2, taskId);

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // ✅ UPDATE - task ini sudah ada tracking
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setInt(1, sessionInc);
                    updatePs.setDouble(2, hoursInc);
                    updatePs.setInt(3, userId);
                    updatePs.setInt(4, taskId);
                    updatePs.executeUpdate();
                    
                    System.out.println("✅ Tracking UPDATED - User: " + userId + ", Task: " + taskId + 
                                     ", Sessions: +" + sessionInc + ", Hours: +" + hoursInc);
                }
            } else {
                // ✅ INSERT - task baru untuk user ini
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setInt(1, userId);
                    insertPs.setInt(2, taskId);
                    insertPs.setInt(3, sessionInc);
                    insertPs.setDouble(4, hoursInc);
                    insertPs.executeUpdate();
                    
                    System.out.println("✅ Tracking INSERTED - User: " + userId + ", Task: " + taskId + 
                                     ", Sessions: " + sessionInc + ", Hours: " + hoursInc);
                }
            }
        }
    }
}

    // ================================
    // DAILY FOCUS (per hari – 7 hari terakhir)
    // ================================
    public static double[] getDailyFocus(int userId) throws SQLException {
        double[] data = new double[7];

        String sql = """
            SELECT DATE(last_updated) as day, SUM(total_focus_hours) as total_hours
            FROM tracking_productivity
            WHERE user_id = ?
            GROUP BY DATE(last_updated)
            ORDER BY DATE(last_updated) DESC
            LIMIT 7
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            int i = 0;
            while (rs.next() && i < 7) {
                data[i++] = rs.getDouble("total_hours");
            }
        }
        return data;
    }

    // ================================
    // WEEKLY FOCUS (4 minggu terakhir)
    // ================================
    public static double[] getWeeklyFocus(int userId) throws SQLException {
        double[] data = new double[4];
        return data; // bisa dikembangkan pakai SQL GROUP BY WEEK()
    }

    // ================================
    // MONTHLY FOCUS (12 bulan terakhir)
    // ================================
    public static double[] getMonthlyFocus(int userId) throws SQLException {
        double[] data = new double[12];
        return data; // bisa dikembangkan pakai GROUP BY MONTH()
    }

    /**
     * Get tracking for specific user (AGGREGATE dari semua task)
     */
    public static TrackingProductivity getByUser(int userId) throws SQLException {
        String sql = """
            SELECT 
                MIN(tracking_id) as tracking_id,
                user_id,
                SUM(total_sessions) as total_sessions,
                SUM(total_focus_hours) as total_focus_hours,
                MAX(last_updated) as last_updated
            FROM tracking_productivity 
            WHERE user_id = ?
            GROUP BY user_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                TrackingProductivity t = new TrackingProductivity();
                t.setTrackingId(rs.getInt("tracking_id"));
                t.setUserId(rs.getInt("user_id"));
                t.setTotalSessions(rs.getInt("total_sessions"));
                t.setTotalFocusHours(rs.getDouble("total_focus_hours"));
                t.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
                return t;
            }
        }
        return null;
    }
    
    public enum LeaderboardSort {
        HOURS("Focus Hours"),
        SESSIONS("Total Sessions"),
        COMPLETED("Completed Tasks");
        
        private final String label;
        
        LeaderboardSort(String label){
            this.label = label;
        }
        
        @Override
        public String toString(){
            return label;
        }
    }

  public static List<Leaderboard> getLeaderboard(LeaderboardSort sort) throws SQLException {
    List<Leaderboard> list = new ArrayList<>();
    
    String orderBy = switch (sort) {
        case SESSIONS -> "totalSessions DESC";
        case HOURS -> "totalFocusHours DESC";
        case COMPLETED -> "completed DESC";
        default -> "totalSessions DESC, totalFocusHours DESC, completed DESC";
    };

    // Gunakan ROUND(SUM(...), 2) untuk membatasi 2 angka di belakang koma
    String sql = """
        SELECT
            u.username,
            COALESCE(SUM(tp.total_sessions), 0) AS totalSessions,
            COALESCE(ROUND(SUM(tp.total_focus_hours), 2), 0) AS totalFocusHours,
            COUNT(DISTINCT CASE WHEN t.completed = 1 THEN t.task_id END) AS completed
        FROM users u
        LEFT JOIN tracking_productivity tp
            ON tp.user_id = u.user_id
        LEFT JOIN tasks t
            ON t.user_id = u.user_id
        GROUP BY u.username
        ORDER BY %s
        LIMIT 10
    """.formatted(orderBy);

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Leaderboard lb = new Leaderboard(
                    rs.getString("username"),
                    rs.getInt("totalSessions"),
                    rs.getDouble("totalFocusHours"), // Data sudah bulat dari database
                    rs.getInt("completed")
            );
            list.add(lb);
        }
    }
    return list;
}
        /**
     * Ambil total sessions user dari semua task
     */
    public static int getTotalSessions(int userId) throws SQLException {
        String sql = """
            SELECT SUM(total_sessions) AS total_sessions
            FROM tracking_productivity
            WHERE user_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_sessions");
            }
        }

        return 0; // default jika belum ada data
    }

}