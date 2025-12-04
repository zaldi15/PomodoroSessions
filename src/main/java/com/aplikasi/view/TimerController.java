package com.aplikasi.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import com.aplikasi.model.Tasks;
import com.aplikasi.dao.TasksDAO;
import com.aplikasi.util.TimerUtil;

public class TimerController {
    @FXML private Label labelTimer, labelTask;
    @FXML private Button btnStart, btnPause, btnReset;
    @FXML private ListView<Tasks> taskListView;

    private Timeline timeline;
    private int timeSeconds = 1500; // 25 menit

    @FXML
    public void initialize() {
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));
        taskListView.getItems().addAll(TasksDAO.getPendingTasks());
    }

    @FXML
    private void handleStart() {
        if (timeline == null || timeline.getStatus() != Timeline.Status.RUNNING) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeSeconds--;
                labelTimer.setText(TimerUtil.formatTime(timeSeconds));
                if (timeSeconds <= 0) {
                    timeline.stop();
                    labelTask.setText("Waktu Habis!");
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    @FXML
    private void handlePause() {
        if (timeline != null) timeline.pause();
    }

    @FXML
    private void handleReset() {
        if (timeline != null) timeline.stop();
        timeSeconds = 1500;
        labelTimer.setText(TimerUtil.formatTime(timeSeconds));
        labelTask.setText("Working On: -");
    }
}
