package com.aplikasi.view;

import com.aplikasi.controller.UserController;
import com.aplikasi.model.User;
import com.aplikasi.util.MainClass;
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
        // Enter key untuk login
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

    if (username.isEmpty() || password.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Input Error", "Username dan password tidak boleh kosong");
        return;
    }

    if (btnLogin != null) {
        btnLogin.setDisable(true);
        btnLogin.setText("Logging in...");
    }

    try {
        User loggedInUser = userController.login(username, password);

        if (loggedInUser != null) {
            System.out.println("✔ Login successful: " + loggedInUser.getUsername());
            clearFields();

            // ✅ PANGGIL MENU UTAMA
            MainClass.openMenuUtama(loggedInUser);

        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah");
            
        }

    } catch (Exception e) {
        System.err.println("❌ Login error: " + e.getMessage());
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Terjadi error saat login");
        
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

