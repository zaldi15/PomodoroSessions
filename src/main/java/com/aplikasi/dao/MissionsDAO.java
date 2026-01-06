package com.aplikasi.dao;

import com.aplikasi.model.Missions;
import com.aplikasi.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MissionsDAO {

    // ==========================================================
    // 1. FUNGSI ADMIN: CRUD Dasar (Tabel 'missions')
    // ==========================================================

    /** * Mengambil semua misi dari master tabel missions.
     * Biasanya digunakan untuk Dashboard Admin.
     */
    public List<Missions> getAllMissionsAdmin() {
        List<Missions> missionList = new ArrayList<>();
        String query = "SELECT id_mission, title, description, target_date FROM missions ORDER BY id_mission DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Status default "Available" karena ini daftar master misi
                missionList.add(new Missions(
                    rs.getInt("id_mission"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("target_date"),
                    "Available" 
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getAllMissionsAdmin: " + e.getMessage());
            e.printStackTrace();
        }
        return missionList;
    }

    /** Menambah misi baru (Admin) */
        public boolean insertMission(String title, String desc, String targetDate) {
            String query = "INSERT INTO missions (title, description, target_date) VALUES (?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, title);
                pstmt.setString(2, desc);
                pstmt.setDate(3, Date.valueOf(targetDate)); // 🔥 FIX PENTING

                return pstmt.executeUpdate() > 0;

            } catch (SQLException e) {
                System.err.println("Error insertMission:");
                e.printStackTrace(); // WAJIB saat debug
                return false;
            }
        }


    /** Memperbarui data misi (Admin) */
    public boolean updateMission(int idMission, String title, String desc, String targetDate) {
        String query = "UPDATE missions SET title = ?, description = ?, target_date = ? WHERE id_mission = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, desc);
            pstmt.setString(3, targetDate);
            pstmt.setInt(4, idMission);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateMission: " + e.getMessage());
            return false; 
        }
    }

    /** Menghapus misi (Admin) */
    public boolean deleteMission(int idMission) {
        String query = "DELETE FROM missions WHERE id_mission = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, idMission);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleteMission: " + e.getMessage());
            return false; 
        }
    }

    // ==========================================================
    // 2. FUNGSI USER: Relasi & Progress (Tabel 'user_missions')
    // ==========================================================

    /** * Mengambil daftar misi khusus untuk user tertentu.
     * Menggunakan LEFT JOIN agar misi yang belum diambil tetap muncul sebagai 'Available'.
     */
    public List<Missions> getMissionsForUser(int userId) {
        List<Missions> list = new ArrayList<>();
        // Query ini menggabungkan tabel missions dengan tabel progress user_missions
        String query = "SELECT m.id_mission, m.title, m.description, m.target_date, " +
                       "IFNULL(um.status, 'Available') as mission_status " +
                       "FROM missions m " +
                       "LEFT JOIN user_missions um ON m.id_mission = um.id_mission AND um.user_id = ? " +
                       "ORDER BY m.id_mission DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Missions(
                        rs.getInt("id_mission"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("target_date"),
                        rs.getString("mission_status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getMissionsForUser: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /** Mengubah status misi menjadi 'In Progress' */
    public boolean startMission(int userId, int idMission) {
        // Menggunakan ON DUPLICATE KEY agar tidak error jika user klik tombol berulang kali
        String query = "INSERT INTO user_missions (user_id, id_mission, status) VALUES (?, ?, 'In Progress') " +
                       "ON DUPLICATE KEY UPDATE status = 'In Progress'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, idMission);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error startMission: " + e.getMessage());
            return false;
        }
    }

    /** Mengubah status misi menjadi 'Completed' */
    public boolean completeMission(int userId, int idMission) {
        String query = "UPDATE user_missions SET status = 'Completed', completed_at = NOW() " +
                       "WHERE user_id = ? AND id_mission = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, idMission);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error completeMission: " + e.getMessage());
            return false;
        }
    }
}