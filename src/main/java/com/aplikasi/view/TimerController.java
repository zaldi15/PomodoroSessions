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
import javafx.event.ActionEvent;


public class TimerController {
    
    @FXML private Label labelTimer;
    @FXML private Label labelTask;
    @FXML private Button btnStart;
    @FXML private Button btnPause;
    @FXML private Button btnReset;
    @FXML private ListView<Tasks> taskListView;
    @FXML private Button btnGoToAdd;
    @FXML private Button btnTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnMissions;
    @FXML private Button btnReport;
    @FXML private Button btnBackToMenu;
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
    private Tasks currentTask;  // Task yang sedang aktif dikerjakan
    private com.aplikasi.model.Missions targetMission;  // Misi yang sedang dikerjakan
    
    
    public void setTargetMission(com.aplikasi.model.Missions mission) {
        this.targetMission = mission;
        if (mission != null) {
            System.out.println("✅ Target mission set: " + mission.getTitle());
            // Update label untuk menampilkan misi yang sedang dikerjakan
            labelTask.setText("🎯 Mission:\n" + mission.getTitle() + "\n📅 " + mission.getTargetDate());
            
            // Disable task selection jika sedang mengerjakan misi
            if (taskListView != null) {
                taskListView.setDisable(true);
            }
        }
    }
    
    private void loadSpinnerStyling() {
    try {
        String css = getClass().getResource(
            "/com/aplikasi/css/Spinner.css"
        ).toExternalForm();
        
        // Apply to scene or spinners
        spinnerMinutes.getStylesheets().add(css);
        spinnerBreak.getStylesheets().add(css);
        
        System.out.println("✅ Spinner CSS loaded");
    } catch (Exception e) {
        System.err.println("❌ Failed to load spinner CSS: " + e.getMessage());
    }
}
    

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
        
        
    loadSpinnerStyling();  // ✅ Add this
 
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
            // Set currentTask saat memulai sesi fokus baru
            currentTask = selectedTask;
            
            timeSeconds = workSeconds;
            
            // Tampilkan info misi jika ada, atau task biasa
            if (targetMission != null) {
                labelTask.setText("🎯 Mission:\n" + targetMission.getTitle());
            } else {
                labelTask.setText(currentTask != null ? "Working On:\n" + currentTask.getTitle() : "Working On: -");
            }
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
        
        // Reset label berdasarkan apakah sedang mengerjakan misi atau tidak
        if (targetMission != null) {
            labelTask.setText("🎯 Mission:\n" + targetMission.getTitle() + "\n📅 " + targetMission.getTargetDate());
        } else {
            labelTask.setText("Working On: -");
            selectedTask = null;
            currentTask = null;
            taskListView.getSelectionModel().clearSelection();
        }
    }

    /**
     * Navigasi ke halaman Manage Task
     */
    
    @FXML
    private void handleTimer() throws IOException {
        System.out.println("Already in Timer");
    }
    
    @FXML
    private void handleGoToManageTask() throws IOException {
        navigateWithUser("/com/aplikasi/view/ManageTask.fxml", btnGoToManageTask);
    }
    
    /**
     * Navigasi ke halaman Report
     */
    @FXML
    private void handleMissions() throws IOException {
        navigateWithUser("/com/aplikasi/view/MissionsView.fxml", btnMissions);
    }
    
    @FXML
    private void handleReport() throws IOException {
        navigateWithUser("/com/aplikasi/view/Report.fxml", btnReport);
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/aplikasi/view/MenuUtama.fxml")
            );

            Stage stage = (Stage) btnBackToMenu.getScene().getWindow();
            loader.load();

            // Jika MainMenu butuh user
            Object controller = loader.getController();
            if (controller instanceof MenuController) {
                ((MenuController) controller).initForUser(currentUser);
            }

            stage.getScene().setRoot(loader.getRoot());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke Menu Utama");
        }
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
            }  else if (controller instanceof ReportController) {   // 👈 tambahkan ini
            ((ReportController) controller).initForUser(currentUser);
        } else if (controller instanceof MissionsController) {
                ((MissionsController) controller).initForUser(currentUser);
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
                if (isCompletedFocusSession && currentTask != null) {
                    double focusHours = workSeconds / 3600.0;

                    TrackingDAO.updateSession(
                        currentUser.getUser_id(),
                        currentTask.getTask_id(),   // 🔑 TASK AKTIF
                        1,                         // 1 sesi fokus
                        focusHours
                    );

                    System.out.println("✅ Tracking productivity berhasil diupdate");
                    System.out.println("   → Task ID     : " + currentTask.getTask_id());
                    System.out.println("   → Focus hours : " + focusHours);
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
