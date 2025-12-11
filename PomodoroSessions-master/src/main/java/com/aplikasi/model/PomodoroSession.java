package com.aplikasi.model;

import java.time.LocalDateTime;

public class PomodoroSession {

    private int sessionId;
    private int userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int focusDuration;
    private int breakDuration;
    private boolean completed;

    // Constructor untuk membuat session baru
    public PomodoroSession(int userId, LocalDateTime startTime, int focusDuration, int breakDuration) {
        this.userId = userId;
        this.startTime = startTime;
        this.focusDuration = focusDuration;
        this.breakDuration = breakDuration;
        this.completed = false;
    }

    // Constructor untuk membaca session dari database
    public PomodoroSession(int sessionId, int userId, LocalDateTime startTime, LocalDateTime endTime,
                           int focusDuration, int breakDuration, boolean completed) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.focusDuration = focusDuration;
        this.breakDuration = breakDuration;
        this.completed = completed;
    }

    // ==== GETTER & SETTER ====
    public int getSessionId() { return sessionId; }
    public int getUserId() { return userId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public int getFocusDuration() { return focusDuration; }
    public void setFocusDuration(int focusDuration) { this.focusDuration = focusDuration; }
    public int getBreakDuration() { return breakDuration; }
    public void setBreakDuration(int breakDuration) { this.breakDuration = breakDuration; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
