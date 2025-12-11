package com.aplikasi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Report {
    private int reportId;
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int completedTasks;
    private double averageFocusHours;
    private LocalDateTime generatedAt;

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

    public double getAverageFocusHours() { return averageFocusHours; }
    public void setAverageFocusHours(double averageFocusHours) { this.averageFocusHours = averageFocusHours; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
