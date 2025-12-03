/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author H P
 */
public class TasksDAO {
    private static final List<Tasks> allTasks = new ArrayList<>();
    private static int nextTasks_id = 1;
    
    public static void insertEntry(Tasks tasks) throws SQLException {
        String SQL = "INSERT INTO tasks (title, deadline, description, completed) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tasks.getTitle());
            stmt.setDate(2, Date.valueOf(tasks.getDeadline())); 
            stmt.setString(3, tasks.getDescription());
            stmt.setBoolean(4, tasks.isCompleted());
            
            int affectedRows = stmt.executeUpdate();

            // Jika berhasil, ambil ID yang baru dibuat
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tasks.setTask_id(rs.getInt(1)); 
                    }
                }
            }
        } 
    }
    
    
    public static List<Tasks> getAllTasks() throws SQLException {
        List<Tasks> dataTasks = new ArrayList<>();
        String SQL = "SELECT title, deadline, description, completed FROM tasks";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tasks task = new Tasks(
                    rs.getString("title"),
                    rs.getDate("deadline").toLocalDate(),
                    rs.getString("description"),
                    rs.getBoolean("completed")
                    // Anda mungkin juga perlu menyertakan ID jika ada
                );
                dataTasks.add(task);
            }
        }
        return dataTasks;
    }

    
    public static void updateEntry(Tasks updateTasks) throws SQLException {
        String SQL = "UPDATE tasks SET title=?, deadline=?, description=?, completed=? WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setString(1, updateTasks.getTitle());
            stmt.setDate(2, Date.valueOf(updateTasks.getDeadline()));
            stmt.setString(3, updateTasks.getDescription());
            stmt.setBoolean(4, updateTasks.isCompleted());
            stmt.setInt(5, updateTasks.getTask_id()); 
            
            stmt.executeUpdate();
        }
    }
    
    public static void removeEntry(Tasks tasks) throws SQLException {
        String SQL = "DELETE FROM tasks WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, tasks.getTask_id());
            
            stmt.executeUpdate();
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author H P
 */
public class TasksDAO {
    private static final List<Tasks> allTasks = new ArrayList<>();
    private static int nextTasks_id = 1;
    
    public static void insertEntry(Tasks tasks) throws SQLException {
        String SQL = "INSERT INTO tasks (title, deadline, description, completed) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tasks.getTitle());
            stmt.setDate(2, Date.valueOf(tasks.getDeadline())); 
            stmt.setString(3, tasks.getDescription());
            stmt.setBoolean(4, tasks.isCompleted());
            
            int affectedRows = stmt.executeUpdate();

            // Jika berhasil, ambil ID yang baru dibuat
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tasks.setTask_id(rs.getInt(1)); 
                    }
                }
            }
        } 
    }
    
    
    public static List<Tasks> getAllTasks() throws SQLException {
        List<Tasks> dataTasks = new ArrayList<>();
        String SQL = "SELECT title, deadline, description, completed FROM tasks";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tasks task = new Tasks(
                    rs.getString("title"),
                    rs.getDate("deadline").toLocalDate(),
                    rs.getString("description"),
                    rs.getBoolean("completed")
                    // Anda mungkin juga perlu menyertakan ID jika ada
                );
                dataTasks.add(task);
            }
        }
        return dataTasks;
    }

    
    public static void updateEntry(Tasks updateTasks) throws SQLException {
        String SQL = "UPDATE tasks SET title=?, deadline=?, description=?, completed=? WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setString(1, updateTasks.getTitle());
            stmt.setDate(2, Date.valueOf(updateTasks.getDeadline()));
            stmt.setString(3, updateTasks.getDescription());
            stmt.setBoolean(4, updateTasks.isCompleted());
            stmt.setInt(5, updateTasks.getTask_id()); 
            
            stmt.executeUpdate();
        }
    }
    
    public static void removeEntry(Tasks tasks) throws SQLException {
        String SQL = "DELETE FROM tasks WHERE task_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            stmt.setInt(1, tasks.getTask_id());
            
            stmt.executeUpdate();
        }
    }
}
