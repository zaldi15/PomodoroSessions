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

/**
 * Data Access Object untuk Tasks.
 * Menangani semua operasi CRUD ke tabel 'tasks' termasuk kolom 'category'.
 */
public class TasksDAO {
    
    /**
     * Menyimpan tugas baru ke database.
     */
    public static void insertEntry(Tasks task, int userId) throws SQLException {
        // Query disesuaikan dengan struktur tabel terbaru
        String SQL = "INSERT INTO tasks (user_id, title, category, description, deadline, completed, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, task.getTitle());
            stmt.setString(3, task.getCategory()); // Menangani Academic, Project, Development, dll.
            stmt.setString(4, task.getDescription());
            stmt.setDate(5, Date.valueOf(task.getDeadline())); 
            stmt.setBoolean(6, task.isCompleted());
            stmt.setTimestamp(7, java.sql.Timestamp.valueOf(task.getCreated_at()));
            
            int affectedRows = stmt.executeUpdate();

            // Ambil ID yang di-generate otomatis oleh MySQL
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        task.setTask_id(rs.getInt(1));
                        task.setUser_id(userId);
                    }
                }
            }
        } 
    }
    
    /**
     * Mengambil semua daftar tugas milik user tertentu.
     */
    public static List<Tasks> getAllTasksByUser(int userId) throws SQLException {
        List<Tasks> dataTasks = new ArrayList<>();
        String SQL = "SELECT * FROM tasks WHERE user_id = ? ORDER BY deadline ASC";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tasks task = new Tasks(
                        rs.getInt("task_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("category"), // Pastikan kolom ini ada di MySQL
                        rs.getDate("deadline").toLocalDate(),
                        rs.getString("description"),
                        rs.getBoolean("completed"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    dataTasks.add(task);
                }
            }
        }
        return dataTasks;
    }

    /**
     * Memperbarui data tugas yang sudah ada.
     */
    public static void updateEntry(Tasks task) throws SQLException {
        String SQL = "UPDATE tasks SET title=?, category=?, description=?, deadline=?, completed=? WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getCategory());
            stmt.setString(3, task.getDescription());
            stmt.setDate(4, Date.valueOf(task.getDeadline()));
            stmt.setBoolean(5, task.isCompleted());
            stmt.setInt(6, task.getTask_id()); 
            
            stmt.executeUpdate();
        }
    }

    /**
     * Menghapus tugas berdasarkan objek Task.
     */
    public static void removeEntry(Tasks task) throws SQLException {
        String SQL = "DELETE FROM tasks WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, task.getTask_id());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mencari satu tugas spesifik berdasarkan ID-nya.
     */
    public static Tasks getTaskById(int taskId) throws SQLException {
        String SQL = "SELECT * FROM tasks WHERE task_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, taskId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tasks(
                        rs.getInt("task_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getDate("deadline").toLocalDate(),
                        rs.getString("description"),
                        rs.getBoolean("completed"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        }
        return null;
    }

    // --- Placeholder untuk Statistik ---
    public static int[] getDailyCompletedTasks(int userId) { return new int[]{2, 3, 4, 1, 5, 2, 3}; }
    public static int[] getWeeklyCompletedTasks(int userId) { return new int[]{10, 14, 11, 18}; }
    public static int[] getMonthlyCompletedTasks(int userId) { return new int[]{20, 25, 18, 30, 40, 35, 45, 38, 50, 55, 60, 70}; }
}
