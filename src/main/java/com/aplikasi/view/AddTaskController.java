package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.util.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller untuk menambahkan tugas baru.
 */
public class AddTasksController implements Initializable {

    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtTitle;
    @FXML
    private DatePicker datePickerDeadline;
    
    @FXML
    private ComboBox<String> cmbCategory; 
    
    @FXML
    private Button btnGoToTimer;
    @FXML
    private Button btnGoToManageTask;
    @FXML
    private Button btnGoToReport;
    
    private ManageTaskController parentController;

    public void setParentController(ManageTaskController manageController){
        this.parentController = manageController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mengisi pilihan kategori
        if (cmbCategory != null) {
            cmbCategory.getItems().addAll("Academic", "Project", "Development");
            cmbCategory.setValue("Academic"); 
        }
    }    

    @FXML
    private void handleAddTask(ActionEvent event) {
        String title = txtTitle.getText();
        String category = cmbCategory.getValue(); 
        LocalDate deadline = datePickerDeadline.getValue();
        String description = txtDescription.getText();

        // 1. Validasi input
        if (title.isEmpty() || category == null || deadline == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Semua kolom harus diisi!");
            return;
        }

        try {
            // 2. Buat objek Tasks (5 parameter sesuai model Tasks.java)
            Tasks baru = new Tasks(title, category, deadline, description, false);
            
            // 3. Simpan ke database melalui DAO 
            // Diberi angka 1 sebagai userId default (sesuai signature di TasksDAO.java)
            TasksDAO.insertEntry(baru, 1); 
            
            // 4. Update tampilan jika dibuka dari halaman ManageTask
            if (parentController != null) {
                parentController.addTaskAndRefresh(baru);
            }
            
            // 5. Feedback Sukses & Tutup Jendela
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil ditambahkan!");
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("Error saat menambah tugas: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menyimpan. Pastikan kolom 'category' sudah ada di database.");
        }
    }

    private void clearFields() {
        txtTitle.clear();
        cmbCategory.setValue("Academic");
        datePickerDeadline.setValue(null);
        txtDescription.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Navigasi Menu ---
    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToTimer.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Timer.fxml");
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToManageTask.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/ManageTask.fxml");
    }

    @FXML
    private void handleGoToReport(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToReport.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Report.fxml");
    }
}
