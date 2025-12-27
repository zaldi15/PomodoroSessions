package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.dao.TrackingDAO;
import com.aplikasi.model.TrackingProductivity;
import com.aplikasi.model.User;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class ReportController {

    @FXML private BarChart<String, Number> barChartTasks;
    @FXML private LineChart<String, Number> lineChartFocus;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private Label lblTotalSessions;
    @FXML private Label lblTotalHours;

    private User currentUser;

    public void initForUser(User user) {
        this.currentUser = user;
        initFilter();
        loadTrackingSummary();
        loadCharts("Daily");
    }

    // ========================
    // Combo Filter (Daily / Weekly / Monthly)
    // ========================
    private void initFilter() {
        cmbFilter.getItems().addAll("Daily", "Weekly", "Monthly");
        cmbFilter.setValue("Daily");

        cmbFilter.setOnAction(e -> {
            loadCharts(cmbFilter.getValue());
        });
    }

    // ========================
    // Summary Section
    // ========================
    private void loadTrackingSummary() {
        try {
            TrackingProductivity t = TrackingDAO.getByUser(currentUser.getId());
            if (t != null) {
                lblTotalSessions.setText(String.valueOf(t.getTotalSessions()));
                lblTotalHours.setText(String.format("%.2f", t.getTotalFocusHours()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================
    // Load Charts
    // ========================
private void loadCharts(String type) {
    try {
        switch (type) {
            case "Weekly" -> {
                loadTasksChart(
                    TasksDAO.getWeeklyCompletedTasks(currentUser.getId()),
                    "Week "
                );
                loadFocusChart(
                    TrackingDAO.getWeeklyFocus(currentUser.getId()),
                    "Week "
                );
            }

            case "Monthly" -> {
                loadTasksChart(
                    TasksDAO.getMonthlyCompletedTasks(currentUser.getId()),
                    "Month "
                );
                loadFocusChart(
                    TrackingDAO.getMonthlyFocus(currentUser.getId()),
                    "Month "
                );
            }

            default -> {
                loadTasksChart(
                    TasksDAO.getDailyCompletedTasks(currentUser.getId()),
                    "Day "
                );
                loadFocusChart(
                    TrackingDAO.getDailyFocus(currentUser.getId()),
                    "Day "
                );
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // ========================
    // Bar Chart (Tasks)
    // ========================
    private void loadTasksChart(int[] data, String labelPrefix) {
        barChartTasks.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Completed Tasks");

        for (int i = 0; i < data.length; i++) {
            series.getData().add(new XYChart.Data<>(labelPrefix + (i + 1), data[i]));
        }

        barChartTasks.getData().add(series);
    }

    // ========================
    // Line Chart (Focus Hours)
    // ========================
    private void loadFocusChart(double[] data, String labelPrefix) {
        lineChartFocus.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Focus Hours");

        for (int i = 0; i < data.length; i++) {
            series.getData().add(new XYChart.Data<>(labelPrefix + (i + 1), data[i]));
        }

        lineChartFocus.getData().add(series);
    }
}
