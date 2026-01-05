package com.aplikasi.dao;

import com.aplikasi.model.PomodoroSession;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.HashMap;



public class SessionDAO {

    
    public static boolean insertSession(PomodoroSession session) {
        // Kolom yang di-insert: user_id, start_time, end_time, focus_duration, break_duration, total_sessions, completed
        // session_id tidak di-insert karena AUTO_INCREMENT
        String sql = """
            INSERT INTO pomodoro_sessions 
            (user_id, start_time, end_time, focus_duration, break_duration, total_sessions, completed)
            VALUES (?, ?, ?, ?, ?, ?, ?)
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
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Session berhasil di-insert ke database");
                return true;
            } else {
                System.out.println("⚠️ Tidak ada row yang ter-insert");
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error insertSession()");
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("❌ Unexpected error insertSession()");
            e.printStackTrace();
            return false;
        }
    }

    
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
                        rs.getInt("total_sessions"),
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

   
    public static int getTotalFocusMinutes(int userId) {
        String sql = "SELECT SUM(focus_duration) AS total FROM pomodoro_sessions WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

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

            if (rs.next()) {
                return rs.getInt("total");
            }

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

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            System.out.println("❌ Error getTotalCompletedSessions()");
            e.printStackTrace();
        }
        return 0;
    }

    
    public static int getTotalSessions(int userId) {
        String sql = "SELECT SUM(total_sessions) AS total FROM pomodoro_sessions WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            System.out.println("❌ Error getTotalSessions()");
            e.printStackTrace();
        }
        return 0;
    }
    
   
    public static int getSessionRecordCount(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM pomodoro_sessions WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            System.out.println("❌ Error getSessionRecordCount()");
            e.printStackTrace();
        }
        return 0;
    }

  
    public static boolean updateSessionCompleted(int sessionId, boolean completed) {
        String sql = "UPDATE pomodoro_sessions SET completed = ? WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, completed);
            stmt.setInt(2, sessionId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error updateSessionCompleted()");
            e.printStackTrace();
            return false;
        }
    }

   
    public static boolean deleteSession(int sessionId) {
        String sql = "DELETE FROM pomodoro_sessions WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error deleteSession()");
            e.printStackTrace();
            return false;
        }
    }
    
    
  
public static Map<String, Double> getGlobalCategoryStats() {
    Map<String, Double> stats = new HashMap<>();

    String sql = "SELECT category, SUM(focus_duration) AS total_minutes " +
                 "FROM pomodoro_sessions " +
                 "GROUP BY category";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String category = rs.getString("category");
            double minutes = rs.getDouble("total_minutes");
            stats.put(category, minutes);
        }

    } catch (Exception e) {
        System.out.println("❌ Error getGlobalCategoryStats()");
        e.printStackTrace();
    }

    return stats;
}


}
