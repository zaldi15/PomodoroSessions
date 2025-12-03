package com.aplikasi.util;

import com.aplikasi.view.DashboardController;
import com.aplikasi.model.User;
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
        openLoginPage();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    // ======================
    // BUKA LOGIN
    // ======================
    public static void openLoginPage() {
        loadScene("/com/aplikasi/view/Login.fxml", "Login - Pomodoro");
        GloballSession.clearSession();
    }

    // ======================
    // BUKA REGISTER
    // ======================
    public static void openRegisterPage() {
        loadScene("/com/aplikasi/view/Register.fxml", "Registrasi");
    }


    public static void main(String[] args) {
        launch(args);
        // Pastikan koneksi ditutup saat aplikasi di-close
        DBConnection.closeConnection(); 
    }

}
