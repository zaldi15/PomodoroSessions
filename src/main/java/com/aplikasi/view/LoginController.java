package com.aplikasi.view;

import com.aplikasi.controller.UserController;
import com.aplikasi.model.User;
import com.aplikasi.util.MainClass;
import com.aplikasi.util.TimerUtil; 
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {
    
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRegister;
    
    private final UserController userController = new UserController();

    @FXML
    public void initialize() {
        // Memungkinkan tekan ENTER untuk login di field username dan password
        if (txtPassword != null) {
            txtPassword.setOnKeyPressed(this::handleKeyPressed);
        }
        if (txtUsername != null) {
            txtUsername.setOnKeyPressed(this::handleKeyPressed);
        }
    }
    
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // 1. Validasi Input Kosong
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Username dan password tidak boleh kosong");
            return;
        }

        // 2. Feedback Visual (Loading)
        if (btnLogin != null) {
            btnLogin.setDisable(true);
            btnLogin.setText("Logging in...");
        }

        try {
            // 3. Proses Autentikasi
            User loggedInUser = userController.login(username, password);

            if (loggedInUser != null) {
                System.out.println("✔ Login successful: " + loggedInUser.getUsername());

                // ==========================================================
                // FITUR PENGINGAT DEADLINE (DINAMIS BERDASARKAN USER ID)
                // ==========================================================
                TimerUtil.showLoginDeadlineAlert(loggedInUser.getId());
                // ==========================================================

                clearFields();

                // 4. Cek Role dan Pindah Halaman
                if (loggedInUser.isAdmin()) {
                    System.out.println("⭐ Akun ADMIN terdeteksi. Membuka Dashboard Admin...");
                    MainClass.openAdminDashboard(loggedInUser); 
                } else {
                    System.out.println("👤 Akun USER terdeteksi. Membuka Menu Utama...");
                    MainClass.openMenuUtama(loggedInUser);
                }

            } else {
                // 5. Jika Login Gagal
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah");
                resetLoginButton();
            }

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Terjadi error saat login ke database");
            resetLoginButton();
        }
    }

    private void resetLoginButton() {
        if (btnLogin != null) {
            btnLogin.setDisable(false);
            btnLogin.setText("Login");
        }
    }

    @FXML
    private void handleGotoRegister() {
        clearFields();
        MainClass.openRegisterPage();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void clearFields() {
        if (txtUsername != null) txtUsername.clear();
        if (txtPassword != null) txtPassword.clear();
    }
}
