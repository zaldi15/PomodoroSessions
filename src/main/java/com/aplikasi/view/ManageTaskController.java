package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.dao.TrackingDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
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

public class ManageTaskController implements Initializable {

    @FXML private TableView<Tasks> tvTasks;
    @FXML private TableColumn<Tasks, String> colTitle;
    @FXML private TableColumn<Tasks, String> colCategory; 
    @FXML private TableColumn<Tasks, String> colDescription;
    @FXML private TableColumn<Tasks, LocalDate> colDeadline;
    @FXML private TableColumn<Tasks, Boolean> colCompleted;
    @FXML private TableColumn<Tasks, Void> colEditDelete;

    @FXML private Button btnGoToAdd, btnGoToTimer, btnGoToManageTask, btnGoToReport;

    private ObservableList<Tasks> dataTasks;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataTasks = FXCollections.observableArrayList();

        // Mapping kolom ke properti Model Tasks
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category")); 
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        setupCompletedColumn();
        setupEditDeleteColumn();

        tvTasks.setItems(dataTasks);
        tvTasks.setEditable(true);
    }

    public void initForUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            refreshTableView();
        }
    }

    /**
     * ✅ INI METODE YANG DICARI ADD_TASK_CONTROLLER
     */
    public void addTaskAndRefresh(Tasks task) {
        if (task != null) {
            dataTasks.add(task); // Tambah ke list UI
            refreshTableView();   // Sinkronkan ulang dengan DB agar ID muncul
        }
    }

    public void refreshTableView() {
        if (currentUser == null) return;
        try {
            dataTasks.setAll(TasksDAO.getAllTasksByUser(currentUser.getUser_id()));
            tvTasks.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data dari database.");
        }
    }

    private void setupCompletedColumn() {
        colCompleted.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        colCompleted.setCellFactory(CheckBoxTableCell.forTableColumn(colCompleted));
        
        // Listener untuk update otomatis saat checkbox diklik
        colCompleted.setOnEditCommit(event -> {
            Tasks task = event.getRowValue();
            task.setCompleted(event.getNewValue());
            try {
                TasksDAO.updateEntry(task);
                if (task.isCompleted()) {
                    TrackingDAO.updateSession(currentUser.getUser_id(), 0, 0);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupEditDeleteColumn() {
        Callback<TableColumn<Tasks, Void>, TableCell<Tasks, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final VBox container = new VBox(5, btnEdit, btnDelete);
            {
                btnEdit.setStyle("-fx-background-color: #6EACDA; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-cursor: hand;");
                container.setStyle("-fx-alignment: center; -fx-padding: 5;");

                btnEdit.setOnAction(e -> openEditWindow(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> confirmAndDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        };
        colEditDelete.setCellFactory(cellFactory);
    }

    private void openEditWindow(Tasks task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/EditTasks.fxml"));
            Stage stage = (Stage) tvTasks.getScene().getWindow();
            stage.getScene().setRoot(loader.load());
            EditTasksController controller = loader.getController();
            controller.initForUser(currentUser);
            controller.setTask(task);
            controller.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmAndDelete(Tasks task) {
        try {
            TasksDAO.removeEntry(task);
            dataTasks.remove(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/AddTask.fxml"));
            Stage stage = (Stage) btnGoToAdd.getScene().getWindow();
            stage.getScene().setRoot(loader.load());
            AddTaskController controller = loader.getController();
            controller.initForUser(currentUser);
            controller.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleGoToTimer(ActionEvent e) throws IOException { navigate("/com/aplikasi/view/Timer.fxml", btnGoToTimer); }
    @FXML private void handleGoToManageTask(ActionEvent e) { System.out.println("Already here"); }
    @FXML private void handleGoToReport(ActionEvent e) throws IOException { navigate("/com/aplikasi/view/TrackingView.fxml", btnGoToReport); }

    private void navigate(String path, Button source) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Stage stage = (Stage) source.getScene().getWindow();
        stage.getScene().setRoot(loader.load());
        Object ctrl = loader.getController();
        if (ctrl instanceof TimerController) ((TimerController) ctrl).initForUser(currentUser);
        if (ctrl instanceof TrackingController) ((TrackingController) ctrl).initForUser(currentUser);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}