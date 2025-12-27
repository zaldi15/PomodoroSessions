package com.aplikasi.model;

import java.time.LocalDateTime;

/**
 * Model untuk Pomodoro Session
 * Merepresentasikan satu sesi fokus/break dalam database
 */
public class PomodoroSession {
    // Field menggunakan snake_case (sesuai nama kolom database)
    private int session_id;
    private int user_id;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private int focus_duration;
    private int break_duration;
    private int total_sessions;
    private boolean completed;
    private String category; // FIELD BARU

    /**
     * Constructor untuk membuat session baru (sebelum disimpan ke database)
     * Ditambahkan parameter category
     */
    public PomodoroSession(int user_id, LocalDateTime start_time, int focus_duration, int break_duration, String category) {
        this.user_id = user_id;
        this.start_time = start_time;
        this.focus_duration = focus_duration;
        this.break_duration = break_duration;
        this.category = category; // Inisialisasi kategori (Belajar/Tugas)
        this.total_sessions = 1;  
        this.completed = false;   
    }

    /**
     * Constructor untuk membaca session dari database (9 parameter)
     * Digunakan oleh DAO saat SELECT dari database
     */
    public PomodoroSession(int session_id, int user_id, LocalDateTime start_time, LocalDateTime end_time,
                           int focus_duration, int break_duration, int total_sessions, boolean completed, String category) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.focus_duration = focus_duration;
        this.break_duration = break_duration;
        this.total_sessions = total_sessions;
        this.completed = completed;
        this.category = category; // Mapping dari kolom database
    }

    // ==== GETTER & SETTER ====
    
    public int getSessionId() { return session_id; }
    public void setSessionId(int session_id) { this.session_id = session_id; }
    
    public int getUserId() { return user_id; }
    public void setUserId(int user_id) { this.user_id = user_id; }
    
    public LocalDateTime getStartTime() { return start_time; }
    public void setStartTime(LocalDateTime start_time) { this.start_time = start_time; }
    
    public LocalDateTime getEndTime() { return end_time; }
    public void setEndTime(LocalDateTime end_time) { this.end_time = end_time; }
    
    public int getFocusDuration() { return focus_duration; }
    public void setFocusDuration(int focus_duration) { this.focus_duration = focus_duration; }
    
    public int getBreakDuration() { return break_duration; }
    public void setBreakDuration(int break_duration) { this.break_duration = break_duration; }
    
    public int getTotalSessions() { return total_sessions; }
    public void setTotalSessions(int total_sessions) { this.total_sessions = total_sessions; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // GETTER & SETTER BARU
    public String getCategory() { 
        return category; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }

    @Override
    public String toString() {
        return "PomodoroSession{" +
                "session_id=" + session_id +
                ", user_id=" + user_id +
                ", category='" + category + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", focus_duration=" + focus_duration +
                ", break_duration=" + break_duration +
                ", total_sessions=" + total_sessions +
                ", completed=" + completed +
                '}';
    }
}