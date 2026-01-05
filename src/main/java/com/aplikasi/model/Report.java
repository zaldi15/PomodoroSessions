package com.aplikasi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class Report {
    private int reportId;
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int completedTasks;
    private double averageFocusHours;
    private int totalSessions;
    private LocalDateTime generatedAt;

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }
    
    
    
   
    private List<CompletedTaskInfo> completedTasksList;
    private String periodType; // "DAILY", "WEEKLY", "MONTHLY"
    
   
    public static class CompletedTaskInfo {
        private int taskId;
        private String taskTitle;
        private String category;
        private LocalDate completedDate;
        private LocalDateTime completedAt;
        private String description;
        private double focusHours;
        
        public CompletedTaskInfo() {}
        
        public CompletedTaskInfo(int taskId, String taskTitle, String category, 
                                LocalDate completedDate, LocalDateTime completedAt,
                                String description, double focusHours) {
            this.taskId = taskId;
            this.taskTitle = taskTitle;
            this.category = category;
            this.completedDate = completedDate;
            this.completedAt = completedAt;
            this.description = description;
            this.focusHours = focusHours;
        }
        
        // Getters and Setters
        public int getTaskId() { return taskId; }
        public void setTaskId(int taskId) { this.taskId = taskId; }
        
        public String getTaskTitle() { return taskTitle; }
        public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public LocalDate getCompletedDate() { return completedDate; }
        public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public double getFocusHours() { return focusHours; }
        public void setFocusHours(double focusHours) { this.focusHours = focusHours; }
        
        
        
        @Override
        public String toString() {
            return taskTitle + " (" + category + ") - " + completedDate;
        }
    }

    // ==================== CONSTRUCTORS ====================
    
    public Report() {}

    /** Constructor untuk data agregasi (Daily/Monthly) */
    public Report(LocalDate startDate, int completedTasks, double averageFocusHours) {
        this.startDate = startDate;
        this.completedTasks = completedTasks;
        this.averageFocusHours = averageFocusHours;
    }
    
    /** Constructor lengkap untuk report snapshot */
    public Report(int userId, LocalDate startDate, LocalDate endDate, 
                  int completedTasks, double averageFocusHours) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.completedTasks = completedTasks;
        this.averageFocusHours = averageFocusHours;
    }

    // ==================== GETTERS & SETTERS ====================

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
    
    // ✅ BARU: Getters & Setters untuk task history
    public List<CompletedTaskInfo> getCompletedTasksList() { return completedTasksList; }
    public void setCompletedTasksList(List<CompletedTaskInfo> completedTasksList) { 
        this.completedTasksList = completedTasksList; 
    }
    
    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    
    // ==================== UTILITY METHODS ====================
    
    
    public String getPeriodDescription() {
        if (startDate == null) return "N/A";
        
        if (startDate.equals(endDate)) {
            return startDate.toString();
        } else {
            return startDate.toString() + " to " + endDate.toString();
        }
    }
    
   
    public String getFormattedFocusHours() {
        return String.format("%.2f hours", averageFocusHours);
    }
    
   
    public double getCompletionRate(int totalTasks) {
        if (totalTasks == 0) return 0.0;
        return (completedTasks * 100.0) / totalTasks;
    }

}
