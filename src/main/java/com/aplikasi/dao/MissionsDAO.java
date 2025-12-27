package com.aplikasi.dao;

import com.aplikasi.model.Missions;
import com.aplikasi.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MissionsDAO {

    /**
     * READ: Mengambil semua data misi dari database
     */
    public List<Missions> getAllMissions() {
        List<Missions> missionList = new ArrayList<>();
        String query = "SELECT id_mission, title, description, target_date FROM missions ORDER BY id_mission DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Missions mission = new Missions(
                    rs.getInt("id_mission"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("target_date")
                );
                missionList.add(mission);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error saat mengambil data misi: " + e.getMessage());
            e.printStackTrace();
        }
        return missionList;
    }

    /**
     * CREATE: Menambah misi baru ke database
     */
    public boolean insertMission(String title, String desc, String targetDate) {
        String query = "INSERT INTO missions (title, description, target_date) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, desc);
            pstmt.setString(3, targetDate);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat menambah misi: " + e.getMessage());
            return false;
        }
    }

    /**
     * DELETE: Menghapus misi berdasarkan ID
     */
    public boolean deleteMission(int idMission) {
        String query = "DELETE FROM missions WHERE id_mission = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, idMission);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat menghapus misi: " + e.getMessage());
            return false;
        }
    }

    /**
     * UPDATE: Memperbarui data misi yang sudah ada
     */
    public boolean updateMission(int idMission, String title, String desc, String targetDate) {
        String query = "UPDATE missions SET title = ?, description = ?, target_date = ? WHERE id_mission = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, desc);
            pstmt.setString(3, targetDate);
            pstmt.setInt(4, idMission);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat memperbarui misi: " + e.getMessage());
            return false;
        }
    }
}