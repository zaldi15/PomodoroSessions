package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.dao.TrackingDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import com.aplikasi.util.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controller untuk Manage Task
 * Menampilkan daftar tugas user dan operasi CRUD
 */
public class ManageTaskController implements Initializable {

    @FXML private TableColumn<Tasks, String> colTitle;
    @FXML private TableColumn<Tasks, String> colDescription;
    @FXML private TableColumn<Tasks, Boolean> colCompleted;
    @FXML private TableView<Tasks> tvTasks;
    @FXML private TableColumn<Tasks, LocalDate> colDeadline;
    @FXML private TableColumn<Tasks, Void> colEditDelete;
    @FXML private Button btnGoToAdd;
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnGoToReport;
    
    private ObservableList<Tasks> dataTasks;
    private User currentUser; // User yang sedang aktif
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataTasks = FXCollections.observableArrayList();
        
        // Setup kolom tabel
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCompleted.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        colCompleted.setCellFactory(column -> {
            return new CheckBoxTableCell<>(index -> {
                Tasks task = tvTasks.getItems().get(index);
                    task.completedProperty().addListener((obs, oldVal, newVal) -> {
        try {
            task.setCompleted(newVal);
            TasksDAO.updateEntry(task);

            // Optional: tracking
            if (newVal) {
                TrackingDAO.updateSession(currentUser.getId(), 0, 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    });
                return task.completedProperty();
            });
        });

        // Setup tombol Edit & Delete
        setupEditDeleteColumn();
        
        // Handler untuk update checkbox
        colCompleted.setOnEditCommit(event -> {
            Tasks task = event.getRowValue();
            task.setCompleted(event.getNewValue());
            try {
                TasksDAO.updateEntry(task);

        // ✅ INJEK TRACKING PRODUKTIVITAS SAAT TASK SELESAI
        if (event.getNewValue()) {
            try {
                TrackingDAO.updateSession(currentUser.getUser_id(), 0, 0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Status tugas berhasil diupdate");
            } catch (SQLException e) {
                System.err.println("Gagal update status completed: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal update status tugas.");
                task.setCompleted(event.getOldValue());
                tvTasks.refresh();
            }
        });
    
        tvTasks.setItems(dataTasks);
        tvTasks.setEditable(true);
    }
    
    /**
     * PENTING: Method ini dipanggil dari MainMenuController
     * untuk menginisialisasi controller dengan user yang sedang login
     */
    public void initForUser(User user) {
        this.currentUser = user;
        System.out.println("➡ ManageTask: User = " + currentUser.getUsername());
        refreshTableView();
    }
    
    /**
     * Setup kolom Edit & Delete dengan tombol
     */
    private void setupEditDeleteColumn() {
    Callback<TableColumn<Tasks, Void>, TableCell<Tasks, Void>> cellFactory = param -> {
        return new TableCell<>() {

            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final VBox container = new VBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(event -> {
                    Tasks task = getTableView().getItems().get(getIndex());
                    if (task != null) {
                        openEditWindow(task);
                    }
                });

                btnDelete.setOnAction(event -> {
                    Tasks task = getTableView().getItems().get(getIndex());
                    if (task != null) {
                        deleteTask(task);
                    }
                });

                container.setStyle("-fx-alignment: center;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);                    // ✅ penting
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);        // ✅ tampilkan tombol
                }
            }
        };
    };

    colEditDelete.setCellFactory(cellFactory);
}

    
    /**
     * Buka window untuk edit task
     */
    private void openEditWindow(Tasks task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/EditTasks.fxml"));
            Stage stage = (Stage) tvTasks.getScene().getWindow();
            
            // Load FXML
            loader.load();
            
            // Ambil controller dan set data
            EditTasksController controller = loader.getController();
            controller.initForUser(currentUser);
            controller.setTask(task);
            controller.setParentController(this);
            
            // Switch scene
            stage.getScene().setRoot(loader.getRoot());
            
        } catch (IOException e) {
            System.err.println("Gagal memuat EditTasks.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka window edit");
        }
    }
    
    /**
     * Hapus task dari database dan tabel
     */
    private void deleteTask(Tasks task) {
        try {
            TasksDAO.removeEntry(task);
            dataTasks.remove(task);
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil dihapus");
        } catch (SQLException e) { 
            System.err.println("Database Error saat menghapus: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menghapus tugas dari database.");
        }
    }
    private void handleEditTask(Tasks task) {
    if (task == null) return;

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/EditTask.fxml"));
        Stage stage = (Stage) tvTasks.getScene().getWindow();

        loader.load();

        // Kirim data ke controller edit
        EditTasksController controller = loader.getController();
        controller.initForUser(currentUser);
        controller.setTask(task);
        controller.setParentController(this);

        // Pindah ke halaman edit
        stage.getScene().setRoot(loader.getRoot());

    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Edit Task");
    }
    
    
}
   



    @FXML
    private void handleAddTask(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/AddTask.fxml"));
            Stage stage = (Stage) btnGoToAdd.getScene().getWindow();
            
            loader.load();
            
            AddTaskController controller = loader.getController();
            controller.initForUser(currentUser);
            controller.setParentController(this);
            
            stage.getScene().setRoot(loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Add Task");
        }
    }
   
    /**
     * Refresh tabel dengan data terbaru dari database
     */
    public void refreshTableView() {
        if (currentUser == null) {
            System.err.println("❌ ManageTask: currentUser is null!");
            return;
        }
        
        dataTasks.clear();
        try {
            dataTasks.addAll(TasksDAO.getAllTasksByUser(currentUser.getId()));
            System.out.println("✔ ManageTask: Loaded " + dataTasks.size() + " tasks for user " + currentUser.getUsername());
        } catch (SQLException e) {
            System.err.println("Gagal memuat data tugas dari database: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar tugas.");
        }
        tvTasks.refresh();
    }
    
    /**
     * Tambah task ke tabel dan refresh
     */
    public void addTaskAndRefresh(Tasks task) {
        if (task != null) {
            dataTasks.add(task);
            tvTasks.refresh();
        }
    }

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Timer.fxml", btnGoToTimer);
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException {
        // Sudah di ManageTask, tidak perlu navigasi
        System.out.println("Already in ManageTask");
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
            if (controller instanceof TimerController) {
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
