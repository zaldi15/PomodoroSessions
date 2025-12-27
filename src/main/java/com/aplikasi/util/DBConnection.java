package com.aplikasi.util; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
 
    
    
    private static final String URL = "jdbc:mysql://localhost:3306/db_pomodoro";
    private static final String USER = "root";       
    private static final String PASSWORD = "";     

    
    private DBConnection() {
        
    }

    
    public static Connection getConnection() {
    try {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    } catch (SQLException e) {
        System.err.println("❌ GAGAL TERHUBUNG KE DATABASE.");
        throw new RuntimeException(e);
    }
}

    
    
    
}