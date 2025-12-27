/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.util.SceneManager;
import java.awt.HeadlessException;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author H P
 */
public class AddTasksController implements Initializable {

    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtTitle;
    @FXML
    private DatePicker datePickerDeadline;
    
    private ManageTaskController parentController;
    @FXML
    private Button btnGoToTimer;
    @FXML
    private Button btnGoToManageTask;
    @FXML
    private Button btnGoToReport;
    
    public void setParentController(ManageTaskController manageController){
        this.parentController = manageController;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

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

    @FXML
    private void handleAddTask(ActionEvent event) {
        String title = txtTitle.getText();
        LocalDate deadline = datePickerDeadline.getValue();
        String description = txtDescription.getText();

        if (title.isEmpty() || deadline==null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Nama, deadline, dan deskripsi harus terisi");
            return;
        }
        try{
            Tasks baru = new Tasks(title, deadline, description, false);
            TasksDAO.insertEntry(baru);
            
            if (parentController != null) {
            parentController.addTaskAndRefresh(baru); // Gunakan method khusus di ManageTaskController
        }
            
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil ditambah");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }catch (Exception e) {
            System.err.println("Terjadi error tak terduga: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Tak Terduga", "Terjadi error saat menambah tugas.");
        }
    }
    private void clearFields() {
        txtTitle.clear();
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
    
}
