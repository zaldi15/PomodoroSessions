package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddTaskController implements Initializable {

    @FXML private TextField txtTitle;
    @FXML private ComboBox<String> comboCategory;
    @FXML private DatePicker datePickerDeadline;
    @FXML private TextArea txtDescription;
    @FXML private Button btnGoToTimer, btnGoToManageTask, btnGoToReport;
    
    private ManageTaskController parentController; // Pastikan tipe class ini benar
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboCategory.setItems(FXCollections.observableArrayList(
            "Academics", "Project", "Development",  "Lainnya"
        ));
        comboCategory.getSelectionModel().selectFirst();
    }
    
    public void initForUser(User user) {
        this.currentUser = user;
    }
    
    public void setParentController(ManageTaskController manageController) {
        this.parentController = manageController;
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session hilang!");
            return;
        }
        
        String title = txtTitle.getText().trim();
        String category = comboCategory.getValue();
        LocalDate deadline = datePickerDeadline.getValue();
        String description = txtDescription.getText().trim();

        if (title.isEmpty() || deadline == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Mohon lengkapi semua data!");
            return;
        }
        
        try {
            // 1. Buat Object Task baru
            Tasks newTask = new Tasks(title, category, deadline, description, false);
            
            // 2. Simpan ke Database
            TasksDAO.insertEntry(newTask, currentUser.getUser_id());
            
            // 3. Panggil method di Parent (ManageTaskController)
            if (parentController != null) {
                parentController.addTaskAndRefresh(newTask); // Sekarang tidak merah lagi
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil ditambahkan!");
            
            // 4. Kembali ke halaman list
            handleGoToManageTask(event);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error", "Gagal menyimpan task.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleGoToTimer(ActionEvent event) throws IOException { navigate("/com/aplikasi/view/Timer.fxml", btnGoToTimer); }
    @FXML private void handleGoToManageTask(ActionEvent event) throws IOException { navigate("/com/aplikasi/view/ManageTask.fxml", btnGoToManageTask); }
    @FXML private void handleGoToReport(ActionEvent event) throws IOException { navigate("/com/aplikasi/view/TrackingView.fxml", btnGoToReport); }

    private void navigate(String path, Button source) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Stage stage = (Stage) source.getScene().getWindow();
        stage.getScene().setRoot(loader.load());
        
        Object controller = loader.getController();
        if (controller instanceof ManageTaskController) ((ManageTaskController) controller).initForUser(currentUser);
        if (controller instanceof TimerController) ((TimerController) controller).initForUser(currentUser);
        if (controller instanceof TrackingController) ((TrackingController) controller).initForUser(currentUser);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}