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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller untuk Edit Task
 * Mengedit tugas yang sudah ada
 */
public class EditTasksController implements Initializable {

    @FXML private TextArea txtDescription;
    @FXML private TextField txtTitle;
    @FXML private DatePicker datePickerDeadline;
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnGoToReport;
    @FXML private Button btnUpdate;

    private ManageTaskController parentController;
    private Tasks taskToEdit;
    private User currentUser;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization jika diperlukan
    }
    
    /**
     * PENTING: Inisialisasi controller dengan user yang sedang aktif
     */
    public void initForUser(User user) {
        this.currentUser = user;
        System.out.println("➡ EditTask: User = " + currentUser.getUsername());
    }
    
    /**
     * Set task yang akan diedit dan isi field dengan data task
     */
    public void setTask(Tasks task) {
        this.taskToEdit = task;
        if (task != null) {
            txtTitle.setText(task.getTitle());
            datePickerDeadline.setValue(task.getDeadline());
            txtDescription.setText(task.getDescription());
            System.out.println("✔ EditTask: Loaded task - " + task.getTitle());
        }
    }
    
    /**
     * Set parent controller untuk callback setelah update
     */
    public void setParentController(ManageTaskController manageController) {
        this.parentController = manageController;
    }

    @FXML
private void handleEditTask(ActionEvent event) {
    if (currentUser == null) {
        showAlert(Alert.AlertType.ERROR, "Error", "User tidak teridentifikasi");
        return;
    }
    
    if (taskToEdit == null) {
        showAlert(Alert.AlertType.WARNING, "Peringatan", "Task tidak ditemukan");
        return;
    }

    String newTitle = txtTitle.getText().trim();
    LocalDate newDeadline = datePickerDeadline.getValue();
    String newDescription = txtDescription.getText().trim();
    
    if (newTitle.isEmpty() || newDeadline == null || newDescription.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Input Error", "Semua field harus terisi");
        return;
    }
    
    if (newDeadline.isBefore(LocalDate.now())) {
        showAlert(Alert.AlertType.WARNING, "Input Error", "Deadline tidak boleh di masa lalu");
        return;
    }
    
    try {
        taskToEdit.setTitle(newTitle);
        taskToEdit.setDeadline(newDeadline);
        taskToEdit.setDescription(newDescription); 

        TasksDAO.updateEntry(taskToEdit);
        
        if (parentController != null) {
            parentController.refreshTableView();
        }
        
        showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil diupdate");
        handleGoToManageTask(event);
        
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal mengupdate tugas.");
    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan.");
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
    private void handleGoToReport(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Report.fxml", btnGoToReport);
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
            } else if (controller instanceof ReportController) {
                ((ReportController) controller).initForUser(currentUser);
            }
            
            stage.getScene().setRoot(loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman");
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