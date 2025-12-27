package com.aplikasi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Model untuk Tasks (Tugas)
 * Mewakili satu tugas dalam aplikasi Pomodoro
 */
public class Tasks {
    private int task_id;
    private int user_id;
    private String title;
    private String description;
    private String category; // FIELD BARU
    private LocalDate deadline;
    private BooleanProperty completed;
    private LocalDateTime created_at;
    
    /**
     * Constructor untuk membuat task baru (sebelum disimpan ke database)
     * Ditambahkan parameter category
     */
    public Tasks(String title, String category, LocalDate deadline, String description, boolean completed) {
        this.title = title;
        this.category = category; // SET CATEGORY
        this.description = description;
        this.deadline = deadline;
        this.completed = new SimpleBooleanProperty(completed);
        this.created_at = LocalDateTime.now();
    }
    
    /**
     * Constructor lengkap (setelah load dari database)
     * Ditambahkan parameter category
     */
    public Tasks(int task_id, int user_id, String title, String category, LocalDate deadline, 
                 String description, boolean completed, LocalDateTime created_at) {
        this.task_id = task_id;
        this.user_id = user_id;
        this.title = title;
        this.category = category; // SET CATEGORY
        this.description = description;
        this.deadline = deadline;
        this.completed = new SimpleBooleanProperty(completed);
        this.created_at = created_at;
    }
    
    // ==================== GETTERS ====================
    
    public int getTask_id() { return task_id; }
    
    public int getUser_id() { return user_id; }
    
    public String getTitle() { return title; }

    public String getCategory() { return category; } // GETTER BARU
    
    public String getDescription() { return description; }
    
    public LocalDate getDeadline() { return deadline; }
    
    public boolean isCompleted() { return completed.get(); }
    
    public BooleanProperty completedProperty() { return completed; }
    
    public LocalDateTime getCreated_at() { return created_at; }
    
    // ==================== SETTERS ====================
    
    public void setTask_id(int task_id) { this.task_id = task_id; }
    
    public void setUser_id(int user_id) { this.user_id = user_id; }
    
    public void setTitle(String title) { this.title = title; }

    public void setCategory(String category) { this.category = category; } // SETTER BARU
    
    public void setDescription(String description) { this.description = description; }
    
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    
    public void setCompleted(boolean completed) { this.completed.set(completed); }
    
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Override toString untuk display di ListView
     * Sekarang menampilkan kategori juga: "Title [Category] - Deadline"
     */
    @Override
    public String toString() {
        return title + " [" + category + "] - " + deadline;
    }
    
    public boolean isOverdue() {
        return !completed.get() && deadline.isBefore(LocalDate.now());
    }
    
    public boolean isDueToday() {
        return deadline.equals(LocalDate.now());
    }
    
    public long getDaysUntilDeadline() {
        return LocalDate.now().until(deadline).getDays();
    }
    
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
}