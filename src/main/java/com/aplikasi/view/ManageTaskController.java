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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author H P
 */
public class ManageTaskController implements Initializable {

    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtTitle;
    @FXML
    private TableColumn<Tasks, String> colTitle;
    @FXML
    private TableColumn<Tasks, String> colDescription;
    @FXML
    private TableColumn<Tasks, Boolean> colCompleted;
    @FXML
    private DatePicker datePickerDeadline;
    @FXML
    private Button btnBack;
    @FXML
    private TableView<Tasks> tvTasks;
    @FXML
    private TableColumn<Tasks, LocalDate> colDeadline;
    
    private ObservableList<Tasks> dataTasks;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataTasks = FXCollections.observableArrayList();
        // Setup TableView Columns (Mencocokkan nama Property di Model)
        // Note: Gunakan "nim" dan "nama" karena kita membuat Getter untuk Property-nya di Mahasiswa.java
        colTitle.setCellValueFactory(new PropertyValueFactory<>("Nama"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("Deadline"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("Deskripsi"));
        colCompleted.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        colCompleted.setCellFactory(tc -> new CheckBoxTableCell<>());
        
        colCompleted.setOnEditCommit(event -> {
            Tasks task = event.getRowValue();
            task.setCompleted(event.getNewValue());
            TasksDAO.updateEntry(task);
        });
    
        //Bind data ke TableView
        tvTasks.setItems(dataTasks);
        tvTasks.setEditable(true);
        
        //Tambahkan Listener untuk menampilkan detail saat baris dipilih
        tvTasks.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showTasksDetails(newValue));
    }    
    
    private void showTasksDetails(Tasks task) {
        if (task != null) {
            txtTitle.setText(task.getTitle());
            datePickerDeadline.setValue(task.getDeadline());
            txtDescription.setText(task.getDescription());
        } else {
            txtTitle.setText("");
            datePickerDeadline.setValue(null);
            txtDescription.setText("");
        }
    }


    @FXML
    private void handleBtnBackHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Login.fxml");
    }

    @FXML
    private void handleEditTask(ActionEvent event) {
        Tasks selectedTasks = tvTasks.getSelectionModel().getSelectedItem();

        if (selectedTasks != null) {
            try{
            selectedTasks.setTitle(txtTitle.getText()); 
            selectedTasks.setDeadline(datePickerDeadline.getValue()); 
            selectedTasks.setDescription(txtDescription.getText());

            TasksDAO.updateEntry(selectedTasks);
            tvTasks.refresh();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil diedit");
            }catch(HeadlessException e) {
                e.printStackTrace();
            } 
        }
        else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih tugas yang ingin diedit");
        }
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
            dataTasks.add(baru);
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil ditambah");
        }catch(HeadlessException e) {
                e.printStackTrace();
            } 
        }

    @FXML
    private void handleDeleteTask(ActionEvent event) {
        Tasks selectedTasks = tvTasks.getSelectionModel().getSelectedItem();
        
        dataTasks.addAll(TasksDAO.getAllTasks());

        if (selectedTasks != null) {
            try{
            TasksDAO.removeEntry(selectedTasks);
                //TasksDAO blm dibuat
            dataTasks.remove(selectedTasks);
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil dihapus");
            }catch(HeadlessException e) {
                e.printStackTrace();
            }
            
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih tugas yang ingin dihapus");
        }

    }
    
    private void clearFields() {
        txtTitle.clear();
        datePickerDeadline.setValue(null);
        txtDescription.clear();
        tvTasks.getSelectionModel().clearSelection();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
   
}
