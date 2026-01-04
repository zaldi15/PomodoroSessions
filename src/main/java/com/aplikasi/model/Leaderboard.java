package com.aplikasi.model;

public class Leaderboard {

    private String username;
    private int totalSessions;
    private double totalFocusHours;
    private int completed;   // ⬅️ jumlah task completed

    public Leaderboard(String username, int totalSessions,
                            double totalFocusHours, int completed) {
        this.username = username;
        this.totalSessions = totalSessions;
        this.totalFocusHours = totalFocusHours;
        this.completed = completed;
    }

    // ===== GETTERS =====

    public String getUsername() {
        return username;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public double getTotalFocusHours() {
        return totalFocusHours;
    }

    // ⬇️ PENTING: nama getter = completed
    public int getCompleted() {
        return completed;
    }

    // ===== SETTERS (optional) =====

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public void setTotalFocusHours(double totalFocusHours) {
        this.totalFocusHours = totalFocusHours;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
