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
import java.sql.SQLException;
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
public class EditTasksController implements Initializable {

    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtTitle;
    @FXML
    private DatePicker datePickerDeadline;
    @FXML
    private Button btnGoToTimer;
    @FXML
    private Button btnGoToManageTask;
    @FXML
    private Button btnGoToReport;

    private ManageTaskController parentController;
    private Tasks taskToEdit;
    private TasksDAO tasksDAO = new TasksDAO();
    
    
    public void setTask(Tasks task){
        this.taskToEdit = task;
        txtTitle.setText(taskToEdit.getTitle());
        datePickerDeadline.setValue(taskToEdit.getDeadline());
        txtDescription.setText(taskToEdit.getDescription());
    }
    
    public void setParentController(ManageTaskController manageController){
        this.parentController = manageController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleEditTask(ActionEvent event) {
        Tasks taskToEdit = this.taskToEdit;

        if (taskToEdit != null) {
            String newTitle = txtTitle.getText();
            LocalDate newDeadline = datePickerDeadline.getValue();
            String newDescription = txtDescription.getText();
            try{
                taskToEdit.setTitle(newTitle);
                taskToEdit.setDeadline(newDeadline);
                taskToEdit.setDescription(newDescription); 

                TasksDAO.updateEntry(taskToEdit);
                if (parentController != null) {
                    parentController.refreshTableView();
                }
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil diedit");
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }catch(SQLException e) {
                System.err.println("Gagal update tugas di database: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                // Tangkap error tak terduga lainnya (jaring pengaman)
                e.printStackTrace();
            }
        }
        else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih tugas yang ingin diedit");
        }
    }

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToTimer.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Timer.fxml");
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToManageTask.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/ManageTask.fxml");
    }

    @FXML
    private void handleGoToReport(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToReport.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Report.fxml");
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
