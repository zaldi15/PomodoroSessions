package com.aplikasi.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Missions {
    private final IntegerProperty idMission;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty targetDate; // Pengganti points

    // Constructor Updated
    public Missions(int idMission, String title, String description, String targetDate) {
        this.idMission = new SimpleIntegerProperty(idMission);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.targetDate = new SimpleStringProperty(targetDate);
    }

    // Getter untuk Property (Penting untuk TableView JavaFX)
    public IntegerProperty idMissionProperty() { return idMission; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty targetDateProperty() { return targetDate; }

    // Getter Biasa
    public int getIdMission() { return idMission.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public String getTargetDate() { return targetDate.get(); }

    // Setter
    public void setTitle(String title) { this.title.set(title); }
    public void setDescription(String description) { this.description.set(description); }
    public void setTargetDate(String targetDate) { this.targetDate.set(targetDate); }
}