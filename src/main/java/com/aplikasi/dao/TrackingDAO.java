package com.aplikasi.dao;

import com.aplikasi.model.Leaderboard;
import com.aplikasi.model.TrackingProductivity;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrackingDAO {

    public static void updateSession(int userId, int sessionInc, double hoursInc) throws SQLException {

    String checkSql = "SELECT tracking_id FROM tracking_productivity WHERE user_id = ?";
    String insertSql = """
        INSERT INTO tracking_productivity (user_id, total_sessions, total_focus_hours, last_updated)
        VALUES (?, ?, ?, NOW())
    """;
    String updateSql = """
        UPDATE tracking_productivity
        SET total_sessions = total_sessions + ?,
            total_focus_hours = total_focus_hours + ?,
            last_updated = NOW()
        WHERE user_id = ?
    """;

    try (Connection conn = DBConnection.getConnection()) {

        // 1. cek apakah sudah ada data
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, userId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // 2. kalau sudah ada → UPDATE
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setInt(1, sessionInc);
                    updatePs.setDouble(2, hoursInc);
                    updatePs.setInt(3, userId);
                    updatePs.executeUpdate();
                }
            } else {
                // 3. kalau belum ada → INSERT
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setInt(1, userId);
                    insertPs.setInt(2, sessionInc);
                    insertPs.setDouble(3, hoursInc);
                    insertPs.executeUpdate();
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
        SELECT DATE(last_updated) as day, total_focus_hours
        FROM tracking_productivity
        WHERE user_id = ?
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        int i = 0;
        while (rs.next() && i < 7) {
            data[i++] = rs.getDouble("total_focus_hours");
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


    public static TrackingProductivity getByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM tracking_productivity WHERE user_id = ?";

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

        String sql = """
            SELECT
                u.username,
                COALESCE(tp.total_sessions, 0) AS totalSessions,
                COALESCE(tp.total_focus_hours, 0) AS totalFocusHours,
                COUNT(t.task_id) AS completed
            FROM users u
            LEFT JOIN tracking_productivity tp
                ON tp.user_id = u.user_id
            LEFT JOIN tasks t
                ON t.user_id = u.user_id
                AND t.completed = 1
            GROUP BY u.username, tp.total_sessions, tp.total_focus_hours
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
                        rs.getDouble("totalFocusHours"),
                        rs.getInt("completed")
                );
                list.add(lb);
            }
        }

        return list;
    }
}
