package com.aplikasi.util;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainClass extends Application {
    
    // Inisialisasi Statis untuk uji koneksi awal
    static {
        // Uji koneksi database saat aplikasi dimuat
        if (DBConnection.getConnection() == null) {
            System.err.println("APLIKASI TIDAK DAPAT DILUNCURKAN: Gagal terhubung ke database.");
            // Di lingkungan nyata, ini harus menampilkan dialog error yang user-friendly
            System.exit(1); 
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
  
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/Register.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Login - Aplikasi Pomodoro");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Gagal memuat FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
        // Pastikan koneksi ditutup saat aplikasi di-close
        DBConnection.closeConnection(); 
    }
}