package com.aplikasi.dao;

import com.aplikasi.model.Tasks;
import com.aplikasi.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class TasksDAO {
    
    
    public static void insertEntry(Tasks tasks, int userId) throws SQLException {
        String SQL = "INSERT INTO tasks (user_id, title, deadline, description, completed, created_at, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, tasks.getTitle());
            stmt.setDate(3, Date.valueOf(tasks.getDeadline())); 
            stmt.setString(4, tasks.getDescription());
            stmt.setBoolean(5, tasks.isCompleted());
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(tasks.getCreated_at()));
            stmt.setString(7, tasks.getCategory()); // ✅ BARU: Simpan kategori
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tasks.setTask_id(rs.getInt(1));
                        tasks.setUser_id(userId);
                    }
                }
            }
        } 
    }
    
 
    public static List<Tasks> getAllTasksByUser(int userId) throws SQLException {
        List<Tasks> dataTasks = new ArrayList<>();
        String SQL = "SELECT task_id, user_id, title, deadline, description, completed, created_at, category FROM tasks WHERE user_id = ? ORDER BY deadline ASC";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Gunakan constructor lengkap dengan kategori
                    Tasks task = new Tasks(
                        rs.getInt("task_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getDate("deadline").toLocalDate(),
                        rs.getString("description"),
                        rs.getBoolean("completed"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("category") 
                    );
                    dataTasks.add(task);
                }
            }
        }
        return dataTasks;
    }

    
    public static void updateEntry(Tasks updateTasks) throws SQLException {
        String SQL = "UPDATE tasks SET title=?, deadline=?, description=?, completed=?, category=? WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setString(1, updateTasks.getTitle());
            stmt.setDate(2, Date.valueOf(updateTasks.getDeadline()));
            stmt.setString(3, updateTasks.getDescription());
            stmt.setBoolean(4, updateTasks.isCompleted());
            stmt.setString(5, updateTasks.getCategory()); 
            stmt.setInt(6, updateTasks.getTask_id()); 
            
            stmt.executeUpdate();
        }
    }
    
   
    public static List<Tasks> getTasksByCategory(int userId, String category) throws SQLException {
        List<Tasks> dataTasks = new ArrayList<>();
        String SQL = "SELECT task_id, user_id, title, deadline, description, completed, created_at, category FROM tasks WHERE user_id = ? AND category = ? ORDER BY deadline ASC";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tasks task = new Tasks(
                        rs.getInt("task_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getDate("deadline").toLocalDate(),
                        rs.getString("description"),
                        rs.getBoolean("completed"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("category")
                    );
                    dataTasks.add(task);
                }
            }
        }
        return dataTasks;
    }
    
  
    public static int getTaskCountByCategory(int userId, String category) throws SQLException {
        String SQL = "SELECT COUNT(*) as count FROM tasks WHERE user_id = ? AND category = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    public static int getCompletedTaskCountByCategory(int userId, String category) throws SQLException {
        String SQL = "SELECT COUNT(*) as count FROM tasks WHERE user_id = ? AND category = ? AND completed = true";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
    
    // DAILY TASKS (7 hari)
    public static int[] getDailyCompletedTasks(int userId) {
        return new int[]{2, 3, 4, 1, 5, 2, 3};
    }

    // WEEKLY TASKS (4 minggu)
    public static int[] getWeeklyCompletedTasks(int userId) {
        return new int[]{10, 14, 11, 18};
    }

    // MONTHLY TASKS (12 bulan)
    public static int[] getMonthlyCompletedTasks(int userId) {
        return new int[]{20, 25, 18, 30, 40, 35, 45, 38, 50, 55, 60, 70};
    }
    
    /**
     * Hapus task dari database
     */
    public static void removeEntry(Tasks tasks) throws SQLException {
        String SQL = "DELETE FROM tasks WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, tasks.getTask_id());
            stmt.executeUpdate();
        }
    }
    
  
    public static Tasks getTaskById(int taskId) throws SQLException {
        String SQL = "SELECT task_id, user_id, title, deadline, description, completed, created_at, category FROM tasks WHERE task_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, taskId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tasks(
                        rs.getInt("task_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getDate("deadline").toLocalDate(),
                        rs.getString("description"),
                        rs.getBoolean("completed"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("category") 
                    );
                }
            }
        }
        return null;
    }

}
