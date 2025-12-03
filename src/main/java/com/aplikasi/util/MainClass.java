package com.aplikasi.util;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClass extends Application {

    private static Stage mainStage;   // Stage utama

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        openLoginPage(); // Pertama kali buka layar Login
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    // ======================
    // BUKA LOGIN
    // ======================
    public static void openLoginPage() {
        loadScene("/com/aplikasi/view/Login.fxml", "Login - Pomodoro");
        // Tidak ada GlobalSession.clearSession();
    }

    // ======================
    // BUKA REGISTER
    // ======================
    public static void openRegisterPage() {
        loadScene("/com/aplikasi/view/Register.fxml", "Registrasi");
    }

    // ======================
    // LOADER GENERIC
    // ======================
    private static void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainClass.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.setTitle(title);
            mainStage.setResizable(false);
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memuat tampilan: " + fxmlPath);
        }
    }

    public static void main(String[] args) {
        launch(args);

        // Tutup koneksi DB ketika aplikasi keluar
        DBConnection.closeConnection();
    }
}
