package com.aplikasi.view;

import com.aplikasi.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller untuk Main Menu (Menu Utama)
 * Hub untuk navigasi ke semua fitur aplikasi
 * 
 * KONSEP PENTING:
 * - Controller ini menerima User dari LoginController
 * - Setiap navigasi ke fitur lain akan melewatkan User tersebut
 * - Semua controller fitur harus memiliki method initForUser(User user)
 */
public class MenuController implements Initializable {

    @FXML private Label lblWelcome;
    @FXML private Button btnManageTask;
    @FXML private Button btnTimer;
    @FXML private Button btnReport;
    @FXML private Button btnLogout;
    
    private User currentUser;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization awal (sebelum user login)
    }
    
    /**
     * PENTING: Method ini WAJIB dipanggil dari LoginController
     * untuk menginisialisasi menu dengan user yang sedang login
     * 
     * @param user User yang berhasil login
     */
    public void initForUser(User user) {
        this.currentUser = user;
        
        // Update UI dengan informasi user
        if (lblWelcome != null) {
            lblWelcome.setText("Welcome, " + currentUser.getUsername() + "! 👋");
        }
        
        System.out.println("✅ MainMenu initialized for user: " + currentUser.getUsername());
    }
    
    /**
     * Navigasi ke Manage Task
     */
    @FXML
    private void handleManageTask(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session tidak valid");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/ManageTask.fxml"));
            Stage stage = (Stage) btnManageTask.getScene().getWindow();
            
            // Load FXML
            loader.load();
            
            // Pass user ke ManageTaskController
            ManageTaskController controller = loader.getController();
            controller.initForUser(currentUser);
            
            // Switch scene
            stage.getScene().setRoot(loader.getRoot());
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka Manage Task");
        }
    }
    
    /**
     * Navigasi ke Timer (Pomodoro)
     */
    @FXML
    private void handleTimer(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session tidak valid");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/Timer.fxml"));
            Stage stage = (Stage) btnTimer.getScene().getWindow();
            
            loader.load();
            
            // Pass user ke TimerController
            TimerController controller = loader.getController();
            controller.initForUser(currentUser);
            
            stage.getScene().setRoot(loader.getRoot());
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka Timer");
        }
    }
    
    /**
     * Navigasi ke Report
     */
    @FXML
    private void handleReport(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session tidak valid");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/Report.fxml"));
            Stage stage = (Stage) btnReport.getScene().getWindow();
            
            loader.load();
            
            // Pass user ke ReportController
            ReportController controller = loader.getController();
            controller.initForUser(currentUser);
            
            stage.getScene().setRoot(loader.getRoot());
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka Report");
        }
    }
    
    /**
     * Logout dan kembali ke Login
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Konfirmasi logout
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Logout");
            confirmation.setHeaderText("Anda yakin ingin logout?");
            confirmation.setContentText("Semua sesi yang sedang berjalan akan dihentikan.");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response.getText().equals("OK")) {
                    try {
                        System.out.println("👋 User " + currentUser.getUsername() + " logged out");
                        
                        // Clear current user
                        currentUser = null;
                        
                        // Kembali ke Login
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/Login.fxml"));
                        Stage stage = (Stage) btnLogout.getScene().getWindow();
                        stage.getScene().setRoot(loader.load());
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Gagal logout");
                    }
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan saat logout");
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