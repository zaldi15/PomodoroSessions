package com.aplikasi.view;

import com.aplikasi.controller.UserController;
import com.aplikasi.model.User;
import com.aplikasi.util.SceneManager; // Class utilitas untuk ganti scene (DIBUAT MANUAL)
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    
    private final UserController userController = new UserController();

    
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        // Panggil Controller (Lapisan Logic)
        User loggedInUser = userController.login(username, password);
        
        if (loggedInUser != null) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses!", "Selamat datang, " + loggedInUser.getUsername());
            
            // TODO: Simpan user ID di Sesi Global sebelum pindah
            // GlobalSession.setCurrentUser(loggedInUser);

            // Ganti Scene ke Dashboard (Tugas Zaldi Arifa)
            try {
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                SceneManager.switchScene(stage, "/com/aplikasi/view/Dashboard.fxml");
            } catch (IOException e) {
                 showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat Dashboard.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password salah.");
        }
    }

    // Metode untuk navigasi ke Registrasi
    @FXML
    private void handleGotoRegister() throws IOException {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Registration.fxml");
    }
    
    // Utilitas untuk menampilkan alert
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}