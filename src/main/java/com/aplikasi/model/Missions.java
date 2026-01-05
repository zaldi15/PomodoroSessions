package com.aplikasi.model;

import javafx.beans.property.*;

public class Missions {
    private final IntegerProperty idMission;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty targetDate;
    private final StringProperty status; 

    // Constructor Updated
    public Missions(int idMission, String title, String description, String targetDate, String status) {
        this.idMission = new SimpleIntegerProperty(idMission);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.targetDate = new SimpleStringProperty(targetDate);
        this.status = new SimpleStringProperty(status);
    }

  
    public IntegerProperty idMissionProperty() { return idMission; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty targetDateProperty() { return targetDate; }
    public StringProperty statusProperty() { return status; }

  
    public int getIdMission() { return idMission.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public String getTargetDate() { return targetDate.get(); }
    public String getStatus() { return status.get(); }

  
    public void setTitle(String title) { this.title.set(title); }
    public void setDescription(String description) { this.description.set(description); }
    public void setTargetDate(String targetDate) { this.targetDate.set(targetDate); }
    public void setStatus(String status) { this.status.set(status); }

}
