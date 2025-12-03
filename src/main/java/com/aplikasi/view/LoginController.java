package com.aplikasi.view;

import com.aplikasi.controller.UserController;
import com.aplikasi.model.User;
import com.aplikasi.util.MainClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private final UserController userController = new UserController();

    @FXML
    private void handleLogin() {

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        User loggedInUser = userController.login(username, password);

        if (loggedInUser != null) {
            // ⛔ Tidak pakai GlobalSession
            // ⛔ Tidak masuk ke dashboard
            
            showAlert(Alert.AlertType.INFORMATION,
                    "Login Berhasil",
                    "Selamat datang, " + loggedInUser.getUsername() + "!");

        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Login Gagal",
                    "Username atau password salah.");
        }
    }

    @FXML
    private void handleGotoRegister() {
        // Tetap ke halaman Register
        MainClass.openRegisterPage();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
