package com.aplikasi.model;

import java.time.LocalDateTime;

public class TrackingProductivity {
    private int trackingId;
    private int userId;
    private int totalSessions;
    private double totalFocusHours;
    private LocalDateTime lastUpdated;

    // Constructor Kosong
    public TrackingProductivity() {
    }

    // Constructor Lengkap
    public TrackingProductivity(int trackingId, int userId, int totalSessions, double totalFocusHours, LocalDateTime lastUpdated) {
        this.trackingId = trackingId;
        this.userId = userId;
        this.totalSessions = totalSessions;
        this.totalFocusHours = totalFocusHours;
        this.lastUpdated = lastUpdated;
    }

    // --- Getter dan Setter ---

    public int getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public double getTotalFocusHours() {
        return totalFocusHours;
    }

    public void setTotalFocusHours(double totalFocusHours) {
        this.totalFocusHours = totalFocusHours;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
