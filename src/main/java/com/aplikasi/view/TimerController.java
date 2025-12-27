package com.aplikasi.view;

import com.aplikasi.dao.SessionDAO;
import com.aplikasi.dao.TasksDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import com.aplikasi.model.PomodoroSession;
import com.aplikasi.util.TimerUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TimerController {

    @FXML private Label labelTimer, labelTask;
    @FXML private Button btnStart, btnPause, btnReset, btnLogout;
    @FXML private ListView<Tasks> taskListView;
    @FXML private Spinner<Integer> spinnerMinutes, spinnerBreak;

    private Timeline timeline;
    private int timeSeconds;
    private int workSeconds = 1500; 
    private int breakSeconds = 300;  
    private boolean isBreak = false;
    private LocalDateTime sessionStart;
    private User currentUser;

    @FXML
    public void initialize() {
        // 1. Inisialisasi Spinner Fokus & Istirahat
        spinnerMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 25));
        spinnerBreak.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 5));

        // Listener update waktu real-time
        spinnerMinutes.valueProperty().addListener((obs, oldVal, newVal) -> {
            workSeconds = newVal * 60;
            if (!isBreak) updateTimerDisplay(workSeconds);
        });

        spinnerBreak.valueProperty().addListener((obs, oldVal, newVal) -> {
            breakSeconds = newVal * 60;
            if (isBreak) updateTimerDisplay(breakSeconds);
        });

        updateTimerDisplay(workSeconds);

        // 2. Listener List Tugas: Menampilkan tugas yang sedang dikerjakan
        taskListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
            if (newTask != null) {
                labelTask.setText("Working On: " + newTask.getTitle());
            } else {
                labelTask.setText("Working On: -");
            }
        });
    }

    public void initForUser(User user) {
        this.currentUser = user;
        loadTasks();
    }

    private void loadTasks() {
        if (currentUser == null) return;
        try {
            var tasks = TasksDAO.getAllTasksByUser(currentUser.getUser_id())
                    .stream().filter(t -> !t.isCompleted()).toList();
            taskListView.setItems(FXCollections.observableArrayList(tasks));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTimerDisplay(int totalSeconds) {
        timeSeconds = totalSeconds;
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));
    }

    @FXML
    private void handleStart() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) return;
        
        if (sessionStart == null) sessionStart = LocalDateTime.now();
        toggleSettings(true);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeSeconds--;
            labelTimer.setText(TimerUtil.formatTime(timeSeconds));

            if (timeSeconds <= 0) {
                processTimerEnd();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void processTimerEnd() {
        timeline.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();

        if (!isBreak) {
            saveSessionToDatabase(true); 
            isBreak = true;
            showAlert("Focus Finished!", "Time for a break.");
            updateTimerDisplay(breakSeconds);
            handleStart(); 
        } else {
            saveSessionToDatabase(false);
            isBreak = false;
            showAlert("Break Finished!", "Ready to focus again?");
            updateTimerDisplay(workSeconds);
            toggleSettings(false);
        }
    }

    private void saveSessionToDatabase(boolean isCompletedFocus) {
        if (currentUser == null) return;

        // OTOMATIS: Ambil kategori dari tugas yang dipilih di list
        Tasks selectedTask = taskListView.getSelectionModel().getSelectedItem();
        String category = (selectedTask != null) ? selectedTask.getCategory() : "General";

        PomodoroSession session = new PomodoroSession(
            0, currentUser.getUser_id(), sessionStart, LocalDateTime.now(),
            workSeconds / 60, breakSeconds / 60, 1, isCompletedFocus, category
        );

        SessionDAO.insertSession(session);
        sessionStart = null; // Reset untuk sesi berikutnya
    }

    private void toggleSettings(boolean disabled) {
        spinnerMinutes.setDisable(disabled);
        spinnerBreak.setDisable(disabled);
        taskListView.setDisable(disabled);
    }

    @FXML private void handlePause() { if (timeline != null) timeline.pause(); }

    @FXML private void handleReset() {
        if (timeline != null) timeline.stop();
        isBreak = false;
        sessionStart = null;
        updateTimerDisplay(spinnerMinutes.getValue() * 60);
        toggleSettings(false);
    }

    @FXML
    private void handleLogout() {
        try {
            if (timeline != null) timeline.stop();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/Login.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleGoToManageTask() throws IOException { navigate("/com/aplikasi/view/ManageTask.fxml"); }
    @FXML private void handleGoToReport() throws IOException { navigate("/com/aplikasi/view/TrackingView.fxml"); }

    private void navigate(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Stage stage = (Stage) labelTimer.getScene().getWindow();
        stage.getScene().setRoot(loader.load());
        
        Object controller = loader.getController();
        if (controller instanceof ManageTaskController) ((ManageTaskController) controller).initForUser(currentUser);
        if (controller instanceof TrackingController) ((TrackingController) controller).initForUser(currentUser);
    }

    private void showAlert(String title, String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }
}