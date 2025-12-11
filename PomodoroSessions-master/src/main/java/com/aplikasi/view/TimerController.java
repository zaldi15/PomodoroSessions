package com.aplikasi.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import com.aplikasi.dao.TasksDAO;
import com.aplikasi.dao.TrackingDAO;
import com.aplikasi.util.TimerUtil;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller untuk Timer (Pomodoro)
 * Menjalankan sesi Pomodoro dengan task yang dipilih
 */
public class TimerController {
    
    @FXML private Label labelTimer;
    @FXML private Label labelTask;
    @FXML private Button btnStart;
    @FXML private Button btnPause;
    @FXML private Button btnReset;
    @FXML private ListView<Tasks> taskListView;
    @FXML private Button btnGoToAdd;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnGoToReport;

    private Timeline timeline;
    private int timeSeconds = 1500; // 25 menit (25 * 60)
    private User currentUser;
    private Tasks selectedTask;

    @FXML
    public void initialize() {
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));
        labelTask.setText("Working On: -");
        
        // Listener untuk pilih task dari list
        if (taskListView != null) {
            taskListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedTask = newVal;
                        labelTask.setText("Working On:\n" + newVal.getTitle());
                        System.out.println("✔ Timer: Selected task - " + newVal.getTitle());
                    }
                }
            );
            
            // Custom cell factory untuk menampilkan task dengan format lebih baik
            taskListView.setCellFactory(lv -> new ListCell<Tasks>() {
                @Override
                protected void updateItem(Tasks item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle() + "\n📅 " + item.getDeadline());
                    }
                }
            });
        }
    }
    
    /**
     * PENTING: Method ini dipanggil untuk inisialisasi controller dengan user
     */
    public void initForUser(User user) {
        this.currentUser = user;
        System.out.println("➡ Timer: User = " + currentUser.getUsername());
        loadPendingTasks();
    }
    
    /**
     * Load task yang belum selesai milik user
     */
    private void loadPendingTasks() {
        if (currentUser == null) {
            System.err.println("❌ Timer: currentUser is null!");
            return;
        }

        try {
            // Ambil semua task milik user
            var allTasks = TasksDAO.getAllTasksByUser(currentUser.getId());

            // Filter task yang pending (completed = false)
            var pendingTasks = allTasks.stream()
                    .filter(task -> !task.isCompleted())
                    .toList();

            // Tampilkan ke ListView
            if (taskListView != null) {
                taskListView.setItems(FXCollections.observableArrayList(pendingTasks));
            }

            System.out.println("✔ Timer: Loaded " + pendingTasks.size() + " pending tasks for user " + currentUser.getUsername());

        } catch (SQLException e) {
            System.err.println("❌ Error loading tasks: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar tugas.");
        }
    }

    @FXML
    private void handleStart() {
    if (selectedTask == null) {
        showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih task terlebih dahulu!");
        return;
    }

    if (currentUser == null) {
        showAlert(Alert.AlertType.ERROR, "Error", "User tidak ditemukan!");
        return;
    }

    // Cegah timer dijalankan dobel
    if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
        return;
    }

    timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
        timeSeconds--;
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));

        if (timeSeconds <= 0) {
            timeline.stop();
            labelTask.setText("⏰ Waktu Habis! Break Time!");

            // ✅ SIMPAN TRACKING PRODUKTIVITAS
            try {
                TrackingDAO.updateSession(
                    currentUser.getId(),   // ✅ konsisten pakai user_id
                    1,                          // +1 sesi
                    0.42                        // 25 menit ≈ 0.42 jam
                );
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            showCompletionAlert();
        }
    }));

    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    System.out.println("▶ Timer started for task: " + selectedTask.getTitle());
}


    @FXML
    private void handlePause() {
        if (timeline != null) {
            timeline.pause();
            System.out.println("⏸ Timer paused");
        }
    }

    @FXML
    private void handleReset() {
        if (timeline != null) {
            timeline.stop();
        }
        timeSeconds = 1500;
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));
        labelTask.setText("Working On: -");
        selectedTask = null;
        
        if (taskListView != null) {
            taskListView.getSelectionModel().clearSelection();
        }
        
        System.out.println("🔄 Timer reset");
    }
    
    
    @FXML
    private void handleGoToManageTask() throws IOException {
        navigateWithUser("/com/aplikasi/view/ManageTask.fxml", btnGoToManageTask);
    }
    
    @FXML
    private void handleGoToReport() throws IOException {
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
            } else if (controller instanceof AddTaskController) {
                AddTaskController addController = (AddTaskController) controller;
                addController.initForUser(currentUser);
            } else if (controller instanceof ReportController) {
                ((ReportController) controller).initForUser(currentUser);
            }
            
            stage.getScene().setRoot(loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman");
        }
    }
    
    private void showCompletionAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pomodoro Complete!");
        alert.setHeaderText("Great Job! 🎉");
        
        if (selectedTask != null) {
            alert.setContentText("You completed a Pomodoro session for:\n" + selectedTask.getTitle() + 
                               "\n\nTime for a 5-minute break!");
        } else {
            alert.setContentText("You completed a Pomodoro session!\n\nTime for a 5-minute break!");
        }
        
        alert.showAndWait();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}