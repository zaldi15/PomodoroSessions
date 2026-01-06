package com.aplikasi.dao;

import com.aplikasi.model.Tasks;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.aplikasi.util.DBConnection;

public class ReminderDAO {
    public List<String> checkDeadlines(int userId) {
        List<String> listPesan = new ArrayList<>();
        // Query: Mencari tugas yang sisa waktunya 0-3 hari lagi
        String sql = "SELECT title, DATEDIFF(deadline, CURDATE()) as sisa " +
                     "FROM tasks WHERE user_id = ? AND deadline >= CURDATE() " +
                     "HAVING sisa <= 3";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int sisa = rs.getInt("sisa");
                String msg = (sisa == 0) ? "HARI INI!" : sisa + " hari lagi";
                listPesan.add("Tugas '" + rs.getString("title") + "' deadline " + msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listPesan;
    }
}