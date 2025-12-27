/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aplikasi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;




public class TrackingProductivity {
    private int trackingId;
    private int userId;
    private int totalSessions;
    private double totalFocusHours;
    private LocalDateTime lastUpdated;

    public int getTrackingId() { return trackingId; }
    public void setTrackingId(int trackingId) { this.trackingId = trackingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public double getTotalFocusHours() { return totalFocusHours; }
    public void setTotalFocusHours(double totalFocusHours) { this.totalFocusHours = totalFocusHours; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}