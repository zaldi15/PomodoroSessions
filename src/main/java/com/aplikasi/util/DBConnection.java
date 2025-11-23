package com.aplikasi.util; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    private static Connection koneksi;
    
    
    private static final String URL = "jdbc:mysql://localhost:3306/db_pomodoro";
    private static final String USER = "root";       
    private static final String PASSWORD = "";     

    
    private DBConnection() {
        
    }

    
    public static Connection getConnection() {
        
        if (koneksi == null) {
            try {
                
                koneksi = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Koneksi database berhasil dibuat.");
                
            } catch (SQLException e) {
                
                System.err.println("❌ GAGAL TERHUBUNG KE DATABASE.");
                System.err.println("Pesan Error: " + e.getMessage());
                
            } 
        }
        return koneksi;
    }
    
    
    public static void closeConnection() {
        if (koneksi != null) {
            try {
                koneksi.close();
                koneksi = null; 
                System.out.println("☑️ Koneksi database ditutup.");
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }
}