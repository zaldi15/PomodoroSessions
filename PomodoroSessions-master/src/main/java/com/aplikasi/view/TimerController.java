package com.aplikasi.view;

import com.aplikasi.dao.SessionDAO;
import com.aplikasi.dao.TrackingDAO;
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
import com.aplikasi.model.PomodoroSession;
import com.aplikasi.util.TimerUtil;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.application.Platform;

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
    @FXML private Spinner<Integer> spinnerMinutes;   // fokus
    @FXML private Spinner<Integer> spinnerBreak;     // break

    
    private Timeline timeline;
    private int timeSeconds;
    private int workSeconds = 60;   // default 1 menit
    private int breakSeconds = 60;  // default 1 menit
    private boolean isBreak = false;
    private LocalDateTime sessionStart;
    private User currentUser;
    private Tasks selectedTask;

    @FXML
    public void initialize() {
        // Spinner fokus (1–120 menit, default 25)
        spinnerMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 25));
        spinnerMinutes.valueProperty().addListener((obs, oldVal, newVal) -> {
            workSeconds = newVal * 60;
            if (!isBreak) {
                timeSeconds = workSeconds;
                labelTimer.setText(TimerUtil.formatTime(timeSeconds));
            }
        });

        // Spinner break (1–30 menit, default 5)
        spinnerBreak.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 5));
        spinnerBreak.valueProperty().addListener((obs, oldVal, newVal) -> {
            breakSeconds = newVal * 60;
            if (isBreak) {
                timeSeconds = breakSeconds;
                labelTimer.setText(TimerUtil.formatTime(timeSeconds));
            }
        });

        timeSeconds = workSeconds;
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));
        labelTask.setText("Working On: -");

        // Listener task selection
        taskListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
            if (newTask != null) {
                selectedTask = newTask;
                labelTask.setText("Working On:\n" + newTask.getTitle());
            } else {
                selectedTask = null;
                labelTask.setText("Working On: -");
            }
        });

        // Custom cell factory untuk tampilan task
        taskListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tasks item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle() + "\n📅 " + item.getDeadline());
            }
        });
    }

    /**
     * Inisialisasi controller dengan user yang login
     */
    public void initForUser(User user) {
        this.currentUser = user;
        loadPendingTasks();
    }

    /**
     * Memuat daftar task yang belum completed
     */
    private void loadPendingTasks() {
        if (currentUser == null) return;
        try {
            var allTasks = TasksDAO.getAllTasksByUser(currentUser.getUser_id());
            var pendingTasks = allTasks.stream().filter(task -> !task.isCompleted()).toList();
            taskListView.setItems(FXCollections.observableArrayList(pendingTasks));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar tugas.");
        }
    }

    /**
     * Handler untuk tombol Start
     * Memulai timer fokus atau break
     */
    @FXML
    private void handleStart() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) return;

        sessionStart = LocalDateTime.now();

        if (!isBreak) {
            timeSeconds = workSeconds;
            labelTask.setText(selectedTask != null ? "Working On:\n" + selectedTask.getTitle() : "Working On: -");
        } else {
            timeSeconds = breakSeconds;
            labelTask.setText("☕ Break Time!");
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeSeconds--;
            labelTimer.setText(TimerUtil.formatTime(timeSeconds));

            if (timeSeconds <= 0) {
                timeline.stop();

                if (!isBreak) {
                    // Selesai fokus → save session (completed = true) → popup break
                    saveSessionAndUpdateTracking(true);
                    isBreak = true;
                    Platform.runLater(() -> {
                        showBreakAlert(spinnerBreak.getValue());
                        handleStart();
                    });
                } else {
                    // Selesai break → save session (completed = false) → popup fokus
                    saveSessionAndUpdateTracking(false);
                    isBreak = false;
                    Platform.runLater(() -> {
                        showFocusAlert(spinnerMinutes.getValue());
                        handleStart();
                    });
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Handler untuk tombol Pause
     */
    @FXML 
    private void handlePause() { 
        if (timeline != null) timeline.pause(); 
    }

    /**
     * Handler untuk tombol Reset
     * Menghentikan timer dan reset semua state
     */
    @FXML
    private void handleReset() {
        if (timeline != null) timeline.stop();
        isBreak = false;
        workSeconds = spinnerMinutes.getValue() * 60;
        breakSeconds = spinnerBreak.getValue() * 60;
        timeSeconds = workSeconds;
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));
        labelTask.setText("Working On: -");
        selectedTask = null;
        taskListView.getSelectionModel().clearSelection();
    }

    /**
     * Navigasi ke halaman Manage Task
     */
    @FXML
    private void handleGoToManageTask() throws IOException {
        navigateWithUser("/com/aplikasi/view/ManageTask.fxml", btnGoToManageTask);
    }
    
    /**
     * Navigasi ke halaman Report
     */
    @FXML
    private void handleGoToReport() throws IOException {
        navigateWithUser("/com/aplikasi/view/TrackingView.fxml", btnGoToReport);
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
            } else if (controller instanceof TrackingController) {
                ((TrackingController) controller).initForUser(currentUser);
            }
            
            stage.getScene().setRoot(loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman");
        }
    }
    
    /**
     * Menyimpan session ke database DAN update tracking productivity
     * @param isCompletedFocusSession true jika sesi fokus selesai, false jika break
     */
    private void saveSessionAndUpdateTracking(boolean isCompletedFocusSession) {
        if (currentUser == null) {
            System.out.println("⚠️ currentUser null, tidak bisa save session");
            return;
        }

        LocalDateTime endTime = LocalDateTime.now();
        
        // Buat object PomodoroSession dengan 8 parameter
        PomodoroSession session = new PomodoroSession(
            0,  // session_id (auto increment, isi 0 saja)
            currentUser.getUser_id(),
            sessionStart,
            endTime,
            workSeconds / 60,      // fokus duration dalam menit
            breakSeconds / 60,     // break duration dalam menit
            1,                     // total_sessions = 1
            isCompletedFocusSession  // completed = true untuk fokus, false untuk break
        );

        try {
            // 1. Insert session ke database
            boolean inserted = SessionDAO.insertSession(session);
            
            if (inserted) {
                System.out.println("✅ Session berhasil disimpan ke database");
                System.out.println("   → Completed: " + isCompletedFocusSession);
                
                // 2. Update tracking productivity HANYA jika sesi fokus selesai
                if (isCompletedFocusSession) {
                    double focusHours = (workSeconds / 60.0) / 60.0; // convert menit ke jam
                    TrackingDAO.updateSession(currentUser.getUser_id(), 1, focusHours);
                    System.out.println("✅ Tracking productivity berhasil diupdate");
                    System.out.println("   → Focus hours: " + focusHours);
                }
            } else {
                System.out.println("❌ Gagal menyimpan session");
                // GUNAKAN Platform.runLater untuk alert
                Platform.runLater(() -> 
                    showAlert(Alert.AlertType.WARNING, "Warning", "Gagal menyimpan session ke database")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error saat menyimpan session atau update tracking");
            e.printStackTrace();
            // GUNAKAN Platform.runLater untuk alert
            Platform.runLater(() -> 
                showAlert(Alert.AlertType.ERROR, "Database Error", 
                    "Terjadi kesalahan saat menyimpan data:\n" + e.getMessage())
            );
        }
    }

    /**
     * Menampilkan alert saat fokus selesai
     */
    private void showBreakAlert(int breakMinutes) {
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Focus Session Complete!");
        alert.setHeaderText("Waktu fokus selesai 🎉");
        alert.setContentText("Saatnya break selama " + breakMinutes + " menit.\nKlik OK untuk memulai break.");
        alert.showAndWait();
    }

    /**
     * Menampilkan alert saat break selesai
     */
    private void showFocusAlert(int focusMinutes) {
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Break Complete!");
        alert.setHeaderText("Break selesai ☕");
        alert.setContentText("Saatnya kembali fokus selama " + focusMinutes + " menit.\nKlik OK untuk memulai sesi fokus.");
        alert.showAndWait();
    }

    /**
     * Helper method untuk menampilkan alert
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}