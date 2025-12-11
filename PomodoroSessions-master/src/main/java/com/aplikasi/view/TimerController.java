package com.aplikasi.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import com.aplikasi.model.PomodoroSession;
import com.aplikasi.dao.TasksDAO;
import com.aplikasi.dao.SessionDAO;
import com.aplikasi.util.TimerUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.awt.Toolkit;

public class TimerController {
    @FXML private Label labelTimer, labelTask;
    @FXML private Button btnStart, btnPause, btnReset;
    @FXML private ListView<Tasks> taskListView;
    @FXML private Spinner<Integer> spinnerMinutes;   // fokus
    @FXML private Spinner<Integer> spinnerBreak;     // break

    private Timeline timeline;
    private int workSeconds = 60;   // default 1 menit
    private int breakSeconds = 60;  // default 1 menit
    private int timeSeconds;
    private boolean isBreak = false;
    private User currentUser;
    private Tasks selectedTask;
    private LocalDateTime sessionStart;

    @FXML
    public void initialize() {
        // Spinner fokus (1–120 menit)
        spinnerMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 25));
        spinnerMinutes.valueProperty().addListener((obs, oldVal, newVal) -> {
            workSeconds = newVal * 60;
            if (!isBreak) {
                timeSeconds = workSeconds;
                labelTimer.setText(TimerUtil.formatTime(timeSeconds));
            }
        });

        // Spinner break (1–30 menit)
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

        // Listener task
        taskListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
            if (newTask != null) {
                selectedTask = newTask;
                labelTask.setText("Working On:\n" + newTask.getTitle());
            } else {
                selectedTask = null;
                labelTask.setText("Working On: -");
            }
        });

        // Tampilan task di ListView
        taskListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tasks item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle() + "\n📅 " + item.getDeadline());
            }
        });
    }

    public void initForUser(User user) {
        this.currentUser = user;
        loadPendingTasks();
    }

    private void loadPendingTasks() {
        if (currentUser == null) return;
        try {
            var allTasks = TasksDAO.getAllTasksByUser(currentUser.getId());
            var pendingTasks = allTasks.stream().filter(task -> !task.isCompleted()).toList();
            taskListView.setItems(FXCollections.observableArrayList(pendingTasks));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar tugas.");
        }
    }

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
                    // selesai kerja → popup break
                    saveSession(false);
                    isBreak = true;
                    Platform.runLater(() -> {
                        showBreakAlert(spinnerBreak.getValue());
                        handleStart();
                    });
                } else {
                    // selesai break → popup fokus
                    saveSession(true);
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

    @FXML private void handlePause() { if (timeline != null) timeline.pause(); }

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

    private void saveSession(boolean completed) {
        if (currentUser == null) return;
        PomodoroSession session = new PomodoroSession(
            0,
            currentUser.getId(),
            sessionStart,
            LocalDateTime.now(),
            workSeconds / 60,
            breakSeconds / 60,
            completed
        );
        SessionDAO.insertSession(session);
    }

    private void showBreakAlert(int breakMinutes) {
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Focus Session Complete!");
        alert.setHeaderText("Waktu fokus selesai 🎉");
        alert.setContentText("Saatnya break selama " + breakMinutes + " menit.\nKlik OK untuk memulai break.");
        alert.showAndWait();
    }

    private void showFocusAlert(int focusMinutes) {
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Break Complete!");
        alert.setHeaderText("Break selesai ☕");
        alert.setContentText("Saatnya kembali fokus selama " + focusMinutes + " menit.\nKlik OK untuk memulai sesi fokus.");
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
