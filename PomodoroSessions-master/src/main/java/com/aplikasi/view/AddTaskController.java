package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import com.aplikasi.util.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller untuk Add Task
 * Menambahkan tugas baru untuk user yang sedang login
 * UPDATED: Menambahkan ComboBox untuk memilih kategori
 */
public class AddTaskController implements Initializable {

    @FXML private TextArea txtDescription;
    @FXML private TextField txtTitle;
    @FXML private DatePicker datePickerDeadline;
    @FXML private ComboBox<String> cbCategory; // ✅ BARU: ComboBox untuk kategori
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnTracking;
    @FXML private Button btnSave;
    
    private ManageTaskController parentController;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ✅ BARU: Inisialisasi ComboBox kategori
        if (cbCategory != null) {
            cbCategory.getItems().addAll("Academic", "Project", "Development");
            cbCategory.setValue("Academic"); // Default ke Academic
            
            // ✅ BARU: Style untuk ComboBox berdasarkan pilihan
            cbCategory.setOnAction(event -> {
                updateCategoryStyle();
            });
            
            updateCategoryStyle();
        }
    }
    
    /**
     * ✅ BARU: Update style ComboBox berdasarkan kategori yang dipilih
     */
    private void updateCategoryStyle() {
        if (cbCategory == null) return;
        
        String category = cbCategory.getValue();
        String style = "";
        
        switch (category) {
            case "Academic":
                style = "-fx-background-color: #4A90E2; -fx-text-fill: white;";
                break;
            case "Project":
                style = "-fx-background-color: #50C878; -fx-text-fill: white;";
                break;
            case "Development":
                style = "-fx-background-color: #FF6B6B; -fx-text-fill: white;";
                break;
        }
        
        cbCategory.setStyle(style);
    }
    
    /**
     * PENTING: Inisialisasi controller dengan user yang sedang aktif
     */
    public void initForUser(User user) {
        this.currentUser = user;
        System.out.println("➡ AddTask: User = " + currentUser.getUsername());
    }
    
    /**
     * Set parent controller untuk callback setelah save
     */
    public void setParentController(ManageTaskController manageController) {
        this.parentController = manageController;
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User tidak teridentifikasi");
            return;
        }
        
        String title = txtTitle.getText().trim();
        LocalDate deadline = datePickerDeadline.getValue();
        String description = txtDescription.getText().trim();
        String category = cbCategory.getValue(); // ✅ BARU: Ambil kategori yang dipilih

        // Validasi input
        if (title.isEmpty() || deadline == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Nama, deadline, dan deskripsi harus terisi");
            return;
        }
        
        // ✅ BARU: Validasi kategori
        if (category == null || category.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Kategori harus dipilih");
            return;
        }
        
        // Validasi deadline tidak boleh di masa lalu
        if (deadline.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Deadline tidak boleh di masa lalu");
            return;
        }
        
        try {
            // ✅ UPDATED: Buat task baru dengan kategori
            Tasks newTask = new Tasks(title, deadline, description, false, category);
            
            // Simpan ke database dengan user_id
            TasksDAO.insertEntry(newTask, currentUser.getId());
            
            // Refresh parent controller jika ada
            if (parentController != null) {
                parentController.addTaskAndRefresh(newTask);
            }
            
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil ditambahkan");
            
            // Kembali ke ManageTask
            handleGoToManageTask(event);
            
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menyimpan tugas ke database.");
        } catch (Exception e) {
            System.err.println("Terjadi error tak terduga: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Tak Terduga", "Terjadi error saat menambah tugas.");
        }
    }

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Timer.fxml", btnGoToTimer);
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/ManageTask.fxml", btnGoToManageTask);
    }

    @FXML
    private void handleTracking(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/TrackingView.fxml", btnTracking);
    }
    
   
    
    /**
     * Helper method untuk navigasi dengan passing user
     */
    private void navigateWithUser(String fxmlPath, Button sourceButton) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            
            loader.load();
            
            // Pass user ke controller baru
            Object controller = loader.getController();
            if (controller instanceof ManageTaskController) {
                ((ManageTaskController) controller).initForUser(currentUser);
            } else if (controller instanceof TimerController) {
                ((TimerController) controller).initForUser(currentUser);
            } else if (controller instanceof TrackingController) {
                ((TrackingController) controller).initForUser(currentUser);
            } 
            
            stage.getScene().setRoot(loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman");
        }
    }
    
    /**
     * ✅ UPDATED: Bersihkan semua field input termasuk kategori
     */
    private void clearFields() {
        txtTitle.clear();
        datePickerDeadline.setValue(null);
        txtDescription.clear();
        cbCategory.setValue("Academic"); // Reset ke default
        updateCategoryStyle();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
