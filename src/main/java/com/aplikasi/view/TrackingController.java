package com.aplikasi.view;

import com.aplikasi.dao.TrackingProductivityDAO;
import com.aplikasi.model.TrackingProductivity;
import com.aplikasi.util.SceneManager; // Utility pindah scene punya kamu
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
import java.util.ResourceBundle;

public class TrackingController implements Initializable {

    // --- Elemen UI dari FXML (Harus sama persis dengan fx:id di FXML) ---
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnGoToReport;

    @FXML private BarChart<String, Number> productivityChart;

    @FXML private Label lblTotalSessions;
    @FXML private Label lblTotalHours;
    @FXML private Label lblAvgFocus;

    // --- Backend Variables ---
    private TrackingProductivityDAO trackingDAO;
    private int currentUserId; // Nanti diambil dari Session Login

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        trackingDAO = new TrackingProductivityDAO();

        // TODO: Ganti angka 1 ini dengan User ID dari Login Session
        // Contoh: this.currentUserId = UserSession.getInstance().getUserId();
        this.currentUserId = 1;

        loadDashboardData();
    }

    private void loadDashboardData() {
        TrackingProductivity stats = trackingDAO.getTrackingStats(currentUserId);

        // Jika user belum punya data, buatkan data awal (0)
        if (stats == null) {
            trackingDAO.initTrackingUser(currentUserId);
            stats = new TrackingProductivity(0, currentUserId, 0, 0.0, null);
        }

        // 1. UPDATE KARTU SUMMARY (BAWAH)
        // Card 1: Total Sesi
        lblTotalSessions.setText(String.valueOf(stats.getTotalSessions()));

        // Card 2: Total Jam (Format 1 desimal, misal 12.5 Jam)
        lblTotalHours.setText(String.format("%.1f Jam", stats.getTotalFocusHours()));

        // Card 3: Rata-rata (Average Focus Time)
        // Rumus: Total Jam / Total Sesi
        double avg = 0;
        if (stats.getTotalSessions() > 0) {
            avg = stats.getTotalFocusHours() / stats.getTotalSessions();
        }
        lblAvgFocus.setText(String.format("%.2f Jam/Sesi", avg));


        // 2. UPDATE GRAFIK (BAR CHART)
        // Karena DB kamu saat ini hanya menyimpan "Total", kita tampilkan Ringkasan
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Overview");

        series.getData().add(new XYChart.Data<>("Total Sesi", stats.getTotalSessions()));
        series.getData().add(new XYChart.Data<>("Total Jam", stats.getTotalFocusHours()));

        productivityChart.getData().clear();
        productivityChart.getData().add(series);
    }

    // --- NAVIGATION HANDLERS (SIDEBAR) ---

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
        // Sudah di halaman Report, tidak perlu aksi apa-apa
    }
}