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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author H P
 */
public class ManageTaskController implements Initializable {

    @FXML
    private TableColumn<Tasks, String> colTitle;
    @FXML
    private TableColumn<Tasks, String> colDescription;
    @FXML
    private TableColumn<Tasks, Boolean> colCompleted;
    @FXML
    private TableView<Tasks> tvTasks;
    @FXML
    private TableColumn<Tasks, LocalDate> colDeadline;
    @FXML
    private TableColumn<Tasks, Void> colEditDelete;
    
    private ObservableList<Tasks> dataTasks;
    @FXML
    private Button btnGoToAdd;
    @FXML
    private Button btnGoToTimer;
    @FXML
    private Button btnGoToManageTask;
    @FXML
    private Button btnGoToReport;
    
   
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
        Callback<TableColumn<Tasks, Void>, TableCell<Tasks, Void>> editDelete = (TableColumn<Tasks, Void> param)->{
            final TableCell<Tasks, Void> cell = new TableCell<>(){
                private final Button btnEdit = new Button("Edit");
                private final Button btnDelete = new Button("Delete");
                private final VBox container = new VBox(5, btnEdit, btnDelete);
                {
                    btnEdit.setOnAction(event -> {
                        Stage stage = (Stage) btnEdit.getScene().getWindow();
                        try {
                            SceneManager.switchScene(stage, "/com/aplikasi/view/AddTasks.fxml");
                        }catch (IOException e) {
                            System.err.println("Gagal memuat AddTasks.fxml: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    
                    btnDelete.setOnAction(event -> {
                        Tasks taskToDelete = getTableView().getItems().get(getIndex());
                        if (taskToDelete != null) {
                            try{
                            TasksDAO.removeEntry(taskToDelete);
                            getTableView().getItems().remove(taskToDelete);
                            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil dihapus");
                            }catch(SQLException e) { 
                                System.err.println("Database Error saat menghapus: " + e.getMessage());
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menghapus tugas dari database.");
                            } catch (Exception e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Error Tak Terduga", "Terjadi error saat menghapus tugas.");
                            }
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih tugas yang ingin dihapus");
                        }
                    });
                }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Tampilkan tombol di sel
                            setGraphic(container); 
                        }
                    }
                };
                return cell;
        };
        
        colEditDelete.setCellFactory(editDelete);
        
        colCompleted.setOnEditCommit(event -> {
            Tasks task = event.getRowValue();
            task.setCompleted(event.getNewValue());
            try {
                TasksDAO.updateEntry(task);
            }catch (SQLException e) {
                System.err.println("Gagal update status completed: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal update status tugas.");
                // Kembalikan nilai ke semula jika update gagal
                task.setCompleted(event.getOldValue());
                tvTasks.refresh();
            }
        });
    
        tvTasks.setItems(dataTasks);
        tvTasks.setEditable(true);
    }    

    @FXML
    private void handleAddTask(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToAdd.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/AddTasks.fxml");
        }
   
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public void refreshTableView(){
        dataTasks.clear();
        try {
        dataTasks.addAll(TasksDAO.getAllTasks());
        }catch (SQLException e){
            System.err.println("Gagal memuat data tugas dari database: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar tugas.");
        }
        tvTasks.refresh();
    }
    
    public void addTaskAndRefresh(Tasks task){
        if(task != null){
            dataTasks.add(task);
            tvTasks.refresh();
        }
    }

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToTimer.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Register.fxml");
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToManageTask.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Register.fxml");
    }

    @FXML
    private void handleGoToReport(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToReport.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Register.fxml");
    }
}
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author H P
 */
public class ManageTaskController implements Initializable {

    @FXML
    private TableColumn<Tasks, String> colTitle;
    @FXML
    private TableColumn<Tasks, String> colDescription;
    @FXML
    private TableColumn<Tasks, Boolean> colCompleted;
    @FXML
    private TableView<Tasks> tvTasks;
    @FXML
    private TableColumn<Tasks, LocalDate> colDeadline;
    @FXML
    private TableColumn<Tasks, Void> colEditDelete;
    
    private ObservableList<Tasks> dataTasks;
    @FXML
    private Button btnGoToAdd;
    @FXML
    private Button btnGoToTimer;
    @FXML
    private Button btnGoToManageTask;
    @FXML
    private Button btnGoToReport;
    
   
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
        Callback<TableColumn<Tasks, Void>, TableCell<Tasks, Void>> editDelete = (TableColumn<Tasks, Void> param)->{
            final TableCell<Tasks, Void> cell = new TableCell<>(){
                private final Button btnEdit = new Button("Edit");
                private final Button btnDelete = new Button("Delete");
                private final VBox container = new VBox(5, btnEdit, btnDelete);
                {
                    btnEdit.setOnAction(event -> {
                        Stage stage = (Stage) btnEdit.getScene().getWindow();
                        try {
                            SceneManager.switchScene(stage, "/com/aplikasi/view/AddTasks.fxml");
                        }catch (IOException e) {
                            System.err.println("Gagal memuat AddTasks.fxml: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    
                    btnDelete.setOnAction(event -> {
                        Tasks taskToDelete = getTableView().getItems().get(getIndex());
                        if (taskToDelete != null) {
                            try{
                            TasksDAO.removeEntry(taskToDelete);
                            getTableView().getItems().remove(taskToDelete);
                            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil dihapus");
                            }catch(SQLException e) { 
                                System.err.println("Database Error saat menghapus: " + e.getMessage());
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menghapus tugas dari database.");
                            } catch (Exception e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Error Tak Terduga", "Terjadi error saat menghapus tugas.");
                            }
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih tugas yang ingin dihapus");
                        }
                    });
                }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Tampilkan tombol di sel
                            setGraphic(container); 
                        }
                    }
                };
                return cell;
        };
        
        colEditDelete.setCellFactory(editDelete);
        
        colCompleted.setOnEditCommit(event -> {
            Tasks task = event.getRowValue();
            task.setCompleted(event.getNewValue());
            try {
                TasksDAO.updateEntry(task);
            }catch (SQLException e) {
                System.err.println("Gagal update status completed: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal update status tugas.");
                // Kembalikan nilai ke semula jika update gagal
                task.setCompleted(event.getOldValue());
                tvTasks.refresh();
            }
        });
    
        tvTasks.setItems(dataTasks);
        tvTasks.setEditable(true);
    }    

    @FXML
    private void handleAddTask(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToAdd.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/AddTasks.fxml");
        }
   
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public void refreshTableView(){
        dataTasks.clear();
        try {
        dataTasks.addAll(TasksDAO.getAllTasks());
        }catch (SQLException e){
            System.err.println("Gagal memuat data tugas dari database: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar tugas.");
        }
        tvTasks.refresh();
    }
    
    public void addTaskAndRefresh(Tasks task){
        if(task != null){
            dataTasks.add(task);
            tvTasks.refresh();
        }
    }

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToTimer.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Register.fxml");
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToManageTask.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/ManageTask.fxml");
    }

    @FXML
    private void handleGoToReport(ActionEvent event) throws IOException{
        Stage stage = (Stage) btnGoToReport.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Register.fxml");
    }
}
                    
