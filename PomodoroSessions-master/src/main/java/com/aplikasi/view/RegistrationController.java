package com.aplikasi.view;

import com.aplikasi.controller.UserController;
import com.aplikasi.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class RegistrationController {
    
    @FXML private TextField txtRegUsername;
    @FXML private PasswordField txtRegPassword;
    @FXML private TextField txtRegEmail;
    @FXML private Button btnRegister;
    
    private final UserController userController = new UserController();

    @FXML
    private void handleRegister() {
        String username = txtRegUsername.getText();
        String password = txtRegPassword.getText();
        String email = txtRegEmail.getText();
        
        String resultMessage = userController.register(username, password, email);
        
        if (resultMessage.contains("berhasil")) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses!", resultMessage);
            // Kembali ke Login
            handleGotoLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal Registrasi", resultMessage);
        }
    }
    
    // Metode untuk navigasi kembali ke Login
    @FXML
    private void handleGotoLogin() {
        try {
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            SceneManager.switchScene(stage, "/com/aplikasi/view/Login.fxml");
        } catch (IOException e) {
             showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat Login.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}