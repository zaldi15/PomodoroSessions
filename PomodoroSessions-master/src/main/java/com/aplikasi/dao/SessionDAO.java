package com.aplikasi.dao;

import com.aplikasi.model.PomodoroSession;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    // =============================================================
    // INSERT SESSION
    // =============================================================
    public static boolean insertSession(PomodoroSession session) {
        String sql = """
            INSERT INTO pomodoro_sessions 
            (user_id, start_time, end_time, focus_duration, break_duration, completed)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, session.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(session.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(session.getEndTime()));
            stmt.setInt(4, session.getFocusDuration());
            stmt.setInt(5, session.getBreakDuration());
            stmt.setBoolean(6, session.isCompleted());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error insertSession()");
            e.printStackTrace();
            return false;
        }
    }

    // =============================================================
    // GET SEMUA SESSION USER
    // =============================================================
    public static List<PomodoroSession> getSessionsByUser(int userId) {
        List<PomodoroSession> list = new ArrayList<>();

        String sql = "SELECT * FROM pomodoro_sessions WHERE user_id = ? ORDER BY start_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                PomodoroSession session = new PomodoroSession(
                        rs.getInt("session_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getInt("focus_duration"),
                        rs.getInt("break_duration"),
                        rs.getBoolean("completed")
                );

                list.add(session);
            }

        } catch (Exception e) {
            System.out.println("❌ Error getSessionsByUser()");
            e.printStackTrace();
        }

        return list;
    }

    // =============================================================
    // STATISTIK QUERY
    // =============================================================

    public static int getTotalFocusMinutes(int userId) {
        String sql = "SELECT SUM(focus_duration) AS total FROM pomodoro_sessions WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt("total");

        } catch (Exception e) {
            System.out.println("❌ Error getTotalFocusMinutes()");
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalBreakMinutes(int userId) {
        String sql = "SELECT SUM(break_duration) AS total FROM pomodoro_sessions WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt("total");

        } catch (Exception e) {
            System.out.println("❌ Error getTotalBreakMinutes()");
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalCompletedSessions(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM pomodoro_sessions WHERE user_id = ? AND completed = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt("total");

        } catch (Exception e) {
            System.out.println("❌ Error getTotalCompletedSessions()");
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalSessions(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM pomodoro_sessions WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt("total");

        } catch (Exception e) {
            System.out.println("❌ Error getTotalSessions()");
            e.printStackTrace();
        }
        return 0;
    }
}
