/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aplikasi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author H P
 */
public class Tasks {
    private int task_id;
    private int user_id;
    private String title;
    private String description;
    private LocalDate deadline;
    private BooleanProperty completed;
    private LocalDateTime created_at;
    
    public Tasks(String title, LocalDate deadline, String description, boolean completed){
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.completed = new SimpleBooleanProperty(completed);
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

    public int getTask_id() {
        return task_id;
    }

    public int getUser_id() {
        return user_id;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public void setCompleted(BooleanProperty completed) {
        this.completed = completed;
    }
    
    
}
