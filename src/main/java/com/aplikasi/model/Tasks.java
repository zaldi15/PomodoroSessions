package com.aplikasi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Tasks {
    private int task_id;
    private int user_id;
    private String title;
    private String description;
    private LocalDate deadline;
    private BooleanProperty completed;
    private LocalDateTime created_at;
    private StringProperty category; 
    
   
    public enum TaskCategory {
        ACADEMIC("Academic"),
        PROJECT("Project"),
        DEVELOPMENT("Development");
        
        private final String displayName;
        
        TaskCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
   
    public Tasks(String title, LocalDate deadline, String description, boolean completed, String category) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.completed = new SimpleBooleanProperty(completed);
        this.category = new SimpleStringProperty(category != null ? category : "Academic"); // Default ke Academic
        this.created_at = LocalDateTime.now();
    }
    
   
    public Tasks(int task_id, int user_id, String title, LocalDate deadline, 
                 String description, boolean completed, LocalDateTime created_at, String category) {
        this.task_id = task_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.completed = new SimpleBooleanProperty(completed);
        this.created_at = created_at;
        this.category = new SimpleStringProperty(category != null ? category : "Academic");
    }
    
    // ==================== GETTERS ====================
    
    public int getTask_id() {
        return task_id;
    }
    
    public int getUser_id() {
        return user_id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDate getDeadline() {
        return deadline;
    }
    
  
    public boolean isCompleted() {
        return completed.get();
    }
    
   
    public BooleanProperty completedProperty() {
        return completed;
    }
    
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    
   
    public String getCategory() {
        return category.get();
    }
    
   
    public StringProperty categoryProperty() {
        return category;
    }
    
    // ==================== SETTERS ====================
    
    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }
    
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
   
    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }
    
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
    
   
    public void setCategory(String category) {
        this.category.set(category != null ? category : "Academic");
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Override toString untuk debugging dan display di ListView
     * Format: "[Category] Title - Deadline"
     */
    @Override
    public String toString() {
        return "[" + category.get() + "] " + title + " - " + deadline;
    }
    
    /**
     * Cek apakah deadline sudah lewat
     */
    public boolean isOverdue() {
        return !completed.get() && deadline.isBefore(LocalDate.now());
    }
    
    /**
     * Cek apakah deadline hari ini
     */
    public boolean isDueToday() {
        return deadline.equals(LocalDate.now());
    }
    
    /**
     * Hitung sisa hari hingga deadline
     * Negative jika sudah lewat
     */
    public long getDaysUntilDeadline() {
        return LocalDate.now().until(deadline).getDays();
    }
    
    /**
     * Get status string untuk display
     */
    public String getStatusText() {
        if (completed.get()) {
            return "✅ Completed";
        } else if (isOverdue()) {
            return "⚠️ Overdue";
        } else if (isDueToday()) {
            return "🔥 Due Today";
        } else {
            long days = getDaysUntilDeadline();
            return "⏰ " + days + " day(s) left";
        }
    }
    
   
    public String getCategoryIcon() {
        switch (category.get()) {
            case "Academic":
                return "📚";
            case "Project":
                return "🎯";
            case "Development":
                return "💻";
            default:
                return "📋";
        }
    }
    
   
    public String getCategoryColor() {
        switch (category.get()) {
            case "Academic":
                return "#4A90E2"; // Biru
            case "Project":
                return "#50C878"; // Hijau
            case "Development":
                return "#FF6B6B"; // Merah
            default:
                return "#95A5A6"; // Abu-abu
        }
    }
}


