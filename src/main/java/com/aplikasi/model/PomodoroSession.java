package com.aplikasi.model;

import java.time.LocalDateTime;


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

   
    public PomodoroSession(int user_id, LocalDateTime start_time, int focus_duration, int break_duration) {
        this.user_id = user_id;
        this.start_time = start_time;
        this.focus_duration = focus_duration;
        this.break_duration = break_duration;
        this.total_sessions = 1;  // Default: setiap session = 1
        this.completed = false;   // Default: belum selesai
    }

   
    public PomodoroSession(int session_id, int user_id, LocalDateTime start_time, LocalDateTime end_time,
                           int focus_duration, int break_duration, int total_sessions, boolean completed) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.focus_duration = focus_duration;
        this.break_duration = break_duration;
        this.total_sessions = total_sessions;
        this.completed = completed;
    }

   
    
    public int getSessionId() { 
        return session_id; 
    }
    
    public void setSessionId(int session_id) {
        this.session_id = session_id;
    }
    
    public int getUserId() { 
        return user_id; 
    }
    
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
    
    public LocalDateTime getStartTime() { 
        return start_time; 
    }
    
    public void setStartTime(LocalDateTime start_time) {
        this.start_time = start_time;
    }
    
    public LocalDateTime getEndTime() { 
        return end_time; 
    }
    
    public void setEndTime(LocalDateTime end_time) { 
        this.end_time = end_time; 
    }
    
    public int getFocusDuration() { 
        return focus_duration; 
    }
    
    public void setFocusDuration(int focus_duration) { 
        this.focus_duration = focus_duration; 
    }
    
    public int getBreakDuration() { 
        return break_duration; 
    }
    
    public void setBreakDuration(int break_duration) { 
        this.break_duration = break_duration; 
    }
    
    public int getTotalSessions() { 
        return total_sessions; 
    }
    
    public void setTotalSessions(int total_sessions) { 
        this.total_sessions = total_sessions; 
    }
    
    public boolean isCompleted() { 
        return completed; 
    }
    
    public void setCompleted(boolean completed) { 
        this.completed = completed; 
    }

    @Override
    public String toString() {
        return "PomodoroSession{" +
                "session_id=" + session_id +
                ", user_id=" + user_id +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", focus_duration=" + focus_duration +
                ", break_duration=" + break_duration +
                ", total_sessions=" + total_sessions +
                ", completed=" + completed +
                '}';
    }

}
