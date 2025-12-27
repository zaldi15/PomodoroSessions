package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
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
 * Controller untuk Edit Task
 * Mengelola perubahan data tugas termasuk kategori
 */
public class EditTasksController implements Initializable {

    @FXML private TextArea txtDescription;
    @FXML private TextField txtTitle;
    @FXML private DatePicker datePickerDeadline;
    @FXML private ComboBox<String> comboCategory; // Tambahan untuk kategori
    
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnGoToReport;

    private ManageTaskController parentController;
    private Tasks taskToEdit;
    private User currentUser;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inisialisasi daftar kategori yang tersedia
        comboCategory.setItems(FXCollections.observableArrayList(
            "Academics", "Project", "Development", "Lainnya"
        ));
    }
    
    /**
     * Inisialisasi controller dengan user yang sedang aktif
     */
    public void initForUser(User user) {
        this.currentUser = user;
        System.out.println("➡ EditTask: User = " + (currentUser != null ? currentUser.getUsername() : "null"));
    }
    
    /**
     * Set task yang akan diedit dan isi field UI dengan data tersebut
     */
    public void setTask(Tasks task) {
        this.taskToEdit = task;
        if (task != null) {
            txtTitle.setText(task.getTitle());
            comboCategory.setValue(task.getCategory()); // Isi kategori lama
            datePickerDeadline.setValue(task.getDeadline());
            txtDescription.setText(task.getDescription());
            System.out.println("✔ EditTask: Loaded task - " + task.getTitle() + " [" + task.getCategory() + "]");
        }
    }
    
    /**
     * Set parent controller untuk callback refresh tabel
     */
    public void setParentController(ManageTaskController manageController) {
        this.parentController = manageController;
    }

    @FXML
    private void handleEditTask(ActionEvent event) {
        // 1. Validasi Identitas
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User tidak teridentifikasi. Silakan login kembali.");
            return;
        }
        
        if (taskToEdit == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Data tugas tidak ditemukan.");
            return;
        }

        // 2. Ambil Data dari Input
        String newTitle = txtTitle.getText().trim();
        String newCategory = comboCategory.getValue(); // Ambil dari ComboBox
        LocalDate newDeadline = datePickerDeadline.getValue();
        String newDescription = txtDescription.getText().trim();
        
        // 3. Validasi Field Kosong
        if (newTitle.isEmpty() || newCategory == null || newDeadline == null || newDescription.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Semua field (Judul, Kategori, Deadline, Deskripsi) harus diisi!");
            return;
        }
        
        // 4. Validasi Tanggal
        if (newDeadline.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Deadline tidak boleh di masa lalu.");
            return;
        }
        
        try {
            // 5. Update Objek Task
            taskToEdit.setTitle(newTitle);
            taskToEdit.setCategory(newCategory); // Simpan kategori baru
            taskToEdit.setDeadline(newDeadline);
            taskToEdit.setDescription(newDescription); 

            // 6. Update Database melalui DAO
            TasksDAO.updateEntry(taskToEdit);
            
            // 7. Callback Refresh Table di halaman Manage Task
            if (parentController != null) {
                parentController.refreshTableView();
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil diperbarui!");
            
            // 8. Kembali ke halaman Manage Task
            handleGoToManageTask(event);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menyimpan perubahan ke database.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan sistem: " + e.getMessage());
        }
    }

    // --- Navigasi ---

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Timer.fxml", btnGoToTimer);
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/ManageTask.fxml", btnGoToManageTask);
    }

    @FXML
    private void handleGoToReport(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Report.fxml", btnGoToReport);
    }
    
    private void navigateWithUser(String fxmlPath, Button sourceButton) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            
            stage.getScene().setRoot(loader.load());
            
            Object controller = loader.getController();
            if (controller instanceof ManageTaskController) {
                ((ManageTaskController) controller).initForUser(currentUser);
            } else if (controller instanceof TimerController) {
                ((TimerController) controller).initForUser(currentUser);
            } else if (controller instanceof ReportController) {
                ((ReportController) controller).initForUser(currentUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat halaman: " + fxmlPath);
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