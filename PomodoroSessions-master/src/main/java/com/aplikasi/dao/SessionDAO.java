package com.aplikasi.dao;

import com.aplikasi.model.PomodoroSession;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk PomodoroSession
 * Menangani semua operasi database untuk tabel pomodoro_sessions
 */
public class SessionDAO {

    // =============================================================
    // INSERT SESSION
    // =============================================================
    /**
     * Menyimpan session baru ke database
     * @param session Object PomodoroSession yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
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

    // =============================================================
    // GET SEMUA SESSION USER
    // =============================================================
    /**
     * Mengambil semua session milik user tertentu, diurutkan dari yang terbaru
     * @param userId ID user
     * @return List of PomodoroSession
     */
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

    // =============================================================
    // STATISTIK QUERY
    // =============================================================

    /**
     * Menghitung total menit fokus user
     * @param userId ID user
     * @return Total menit fokus
     */
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

    /**
     * Menghitung total menit break user
     * @param userId ID user
     * @return Total menit break
     */
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

    /**
     * Menghitung total sesi yang completed (sesi fokus yang selesai)
     * @param userId ID user
     * @return Total completed sessions
     */
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

    /**
     * Menghitung total sessions (SUM dari kolom total_sessions)
     * @param userId ID user
     * @return Total sessions
     */
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
    
    /**
     * Menghitung jumlah record session (COUNT bukan SUM)
     * @param userId ID user
     * @return Jumlah record
     */
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

    /**
     * Update session tertentu (misalnya set completed = true)
     * @param sessionId ID session
     * @param completed Status completed
     * @return true jika berhasil
     */
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

    /**
     * Hapus session berdasarkan ID
     * @param sessionId ID session yang akan dihapus
     * @return true jika berhasil
     */
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
}