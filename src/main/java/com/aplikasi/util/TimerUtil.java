package com.aplikasi.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimerUtil {
    private Timeline timeline;
    private int secondsRemaining;
    private final int userId; // Mengacu pada user_id di database

    public TimerUtil(int userId) {
        this.userId = userId;
    }

    /**
     * FUNGSI 1: Format Detik ke MM:SS
     */
    public static String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * FUNGSI 2: Pengingat Deadline Saat Login
     * Panggil ini tepat setelah proses Login berhasil.
     */
    public static void showLoginDeadlineAlert(int userId) {
        // Query untuk mencari tugas yang deadline-nya 0-3 hari lagi
        String sql = "SELECT title, DATEDIFF(deadline, CURDATE()) as sisa_hari " +
                     "FROM tasks WHERE user_id = ? AND deadline >= CURDATE() " +
                     "HAVING sisa_hari <= 3";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<String> messages = new ArrayList<>();

            while (rs.next()) {
                int sisa = rs.getInt("sisa_hari");
                String info = (sisa == 0) ? "HARI INI!" : sisa + " hari lagi";
                messages.add("• " + rs.getString("title") + " (Deadline: " + info + ")");
            }

            if (!messages.isEmpty()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Pengingat Deadline");
                    alert.setHeaderText("Ada tugas yang mendekati deadline!");
                    alert.setContentText(String.join("\n", messages));
                    alert.show();
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * FUNGSI 3: Menjalankan Timer Pomodoro
     */
    public void startTimer(int initialSeconds, Runnable onTickUpdate) {
        this.secondsRemaining = initialSeconds;
        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;
            onTickUpdate.run();

            // Munculkan pesan semangat saat sisa 1 menit
            if (secondsRemaining == 60) {
                showSessionReminder("NEAR_END");
            }

            // Munculkan alert saat waktu habis
            if (secondsRemaining <= 0) {
                timeline.stop();
                showSessionReminder("FINISHED");
            }
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * FUNGSI 4: Mengambil Pesan Semangat dari tabel 'reminders'
     */
    private void showSessionReminder(String type) {
        String sql = "SELECT message FROM reminders WHERE user_id = ? AND sent = 0 LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, this.userId);
            ResultSet rs = ps.executeQuery();

            String pesan = (type.equals("NEAR_END")) ? "Dikit lagi selesai, semangat!" : "Sesi selesai! Istirahat sejenak.";
            
            if (rs.next()) {
                pesan = rs.getString("message"); // Ambil pesan kustom dari DB
            }

            String finalMsg = pesan;
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pomodoro Reminder");
                alert.setHeaderText(null);
                alert.setContentText(finalMsg);
                alert.show();
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stopTimer() {
        if (timeline != null) timeline.stop();
    }
}