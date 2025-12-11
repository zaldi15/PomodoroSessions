package com.aplikasi.view;

import com.aplikasi.dao.TrackingDAO;
import com.aplikasi.model.TrackingProductivity;
import com.aplikasi.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TrackingController implements Initializable {

    // --- Elemen UI ---
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnGoToReport;

    @FXML private BarChart<String, Number> productivityChart;

    @FXML private Label lblTotalSessions;
    @FXML private Label lblTotalHours;
    @FXML private Label lblAvgFocus;

    // --- Filter Buttons (Daily/Weekly/Monthly) ---
    @FXML private Button btnDaily;
    @FXML private Button btnWeekly;
    @FXML private Button btnMonthly;

    // --- Variabel Data ---
    private int currentUserId = 1; // Default User ID (Nanti diganti Session)

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Muat data ringkasan (Kartu di bawah)
        loadSummaryData();

        // 2. Muat grafik default (Daily)
        handleFilterDaily(null);
    }

    /**
     * Mengambil data total (Summary Cards) menggunakan TrackingDAO.getByUser
     */
    private void loadSummaryData() {
        try {
            // Panggil method STATIC dari DAO baru
            TrackingProductivity stats = TrackingDAO.getByUser(currentUserId);

            if (stats != null) {
                // Update UI Kartu
                lblTotalSessions.setText(String.valueOf(stats.getTotalSessions()));
                lblTotalHours.setText(String.format("%.1f Jam", stats.getTotalFocusHours()));

                // Hitung Rata-rata
                double avg = 0;
                if (stats.getTotalSessions() > 0) {
                    avg = stats.getTotalFocusHours() / stats.getTotalSessions();
                }
                lblAvgFocus.setText(String.format("%.2f Jam/Sesi", avg));
            } else {
                // Data kosong
                lblTotalSessions.setText("0");
                lblTotalHours.setText("0 Jam");
                lblAvgFocus.setText("0 Jam");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat data user: " + e.getMessage());
        }
    }

    /**
     * Logika Grafik: Mengambil array double[] dari DAO dan menampilkannya ke BarChart
     */
    private void updateChart(String title, double[] data) {
        productivityChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);

        for (int i = 0; i < data.length; i++) {
            // Label sumbu X: "Data 1", "Data 2", dst.
            String label = String.valueOf(i + 1);
            series.getData().add(new XYChart.Data<>(label, data[i]));
        }

        productivityChart.getData().add(series);
    }

    // --- HANDLER BUTTON FILTER (Daily / Weekly / Monthly) ---

    @FXML
    private void handleFilterDaily(ActionEvent event) {
        try {
            // Panggil getDailyFocus dari DAO
            double[] dailyData = TrackingDAO.getDailyFocus(currentUserId);
            updateChart("Fokus Harian (7 Hari Terakhir)", dailyData);
            setActiveButton(btnDaily);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilterWeekly(ActionEvent event) {
        try {
            // Panggil getWeeklyFocus dari DAO
            double[] weeklyData = TrackingDAO.getWeeklyFocus(currentUserId);
            updateChart("Fokus Mingguan (4 Minggu Terakhir)", weeklyData);
            setActiveButton(btnWeekly);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilterMonthly(ActionEvent event) {
        try {
            // Panggil getMonthlyFocus dari DAO
            double[] monthlyData = TrackingDAO.getMonthlyFocus(currentUserId);
            updateChart("Fokus Bulanan (1 Tahun Terakhir)", monthlyData);
            setActiveButton(btnMonthly);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Helper UI ---

    private void setActiveButton(Button activeBtn) {
        // Reset style semua tombol filter agar terlihat mana yang aktif
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: gray;";
        String activeStyle = "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-background-radius: 20;";

        // Pastikan button tidak null (untuk inisialisasi awal)
        if(btnDaily != null) btnDaily.setStyle(defaultStyle);
        if(btnWeekly != null) btnWeekly.setStyle(defaultStyle);
        if(btnMonthly != null) btnMonthly.setStyle(defaultStyle);

        if (activeBtn != null) {
            activeBtn.setStyle(activeStyle);
        }
    }

    // --- NAVIGASI ---

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToTimer.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/Timer.fxml");
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnGoToManageTask.getScene().getWindow();
        SceneManager.switchScene(stage, "/com/aplikasi/view/ManageTask.fxml");
    }

    @FXML
    private void handleGoToReport(ActionEvent event) {
        // Stay here
    }
}
