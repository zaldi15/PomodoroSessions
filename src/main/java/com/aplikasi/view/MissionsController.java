package com.aplikasi.view;

import com.aplikasi.dao.MissionsDAO;
import com.aplikasi.model.Missions;
import com.aplikasi.model.User;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MissionsController {

    @FXML private TableView<Missions> tableMissions;
    @FXML private TableColumn<Missions, String> colTitle;
    @FXML private TableColumn<Missions, String> colDesc;
    @FXML private TableColumn<Missions, String> colDate;
    @FXML private TableColumn<Missions, String> colStatus;
    @FXML private Button btnAction;
    @FXML private Button btnBackToMenu;

    private final MissionsDAO missionsDAO = new MissionsDAO();
    private final ObservableList<Missions> missionData = FXCollections.observableArrayList();
    
    private User currentUser;
    private int currentUserId; 

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("targetDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableMissions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateButtonUI(newSelection.getStatus());
            } else {
                btnAction.setDisable(true);
                btnAction.setText("Select a Mission");
            }
        });

        btnAction.setDisable(true);
    }

    public void initForUser(User user) {
        this.currentUser = user;
        if (user != null) {
            this.currentUserId = user.getUser_id();
            loadMissions();
        }
    }

    private void loadMissions() {
        missionData.clear();
        missionData.addAll(missionsDAO.getMissionsForUser(currentUserId));
        tableMissions.setItems(missionData);
        tableMissions.refresh();
    }

    private void updateButtonUI(String status) {
        if (status == null) return;
        btnAction.setDisable(false);
        switch (status) {
            case "Available": btnAction.setText("Start Mission"); break;
            case "In Progress": btnAction.setText("Continue Mission"); break;
            case "Completed": btnAction.setText("Finished"); btnAction.setDisable(true); break;
            default: btnAction.setDisable(true); break;
        }
    }

   
    @FXML
    private void handleActionButton(ActionEvent event) {
        Missions selected = tableMissions.getSelectionModel().getSelectedItem();
        if (selected == null || currentUser == null) return;

        // 1. Jika masih "Available", ubah dulu statusnya di Database menjadi "In Progress"
        if ("Available".equals(selected.getStatus())) {
            missionsDAO.startMission(currentUserId, selected.getIdMission());
        }

        // 2. Langsung pindah ke halaman Timer untuk mengerjakan misi
        goToTimer(event, selected);
    }

    private void goToTimer(ActionEvent event, Missions mission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/Timer.fxml"));
            Parent root = loader.load();

            // Kirim data User DAN data Misi ke TimerController
            TimerController timerController = loader.getController();
            timerController.initForUser(currentUser);
            timerController.setTargetMission(mission);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            
            System.out.println("🚀 Navigating to Timer for mission: " + mission.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman Timer: " + e.getMessage());
        }
    }
    
   
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/aplikasi/view/MenuUtama.fxml")
            );

            Stage stage = (Stage) btnBackToMenu.getScene().getWindow();
            loader.load();

            // Pass user ke MenuController jika ada
            Object controller = loader.getController();
            if (controller instanceof MenuController) {
                ((MenuController) controller).initForUser(currentUser);
            }

            stage.getScene().setRoot(loader.getRoot());
            
            System.out.println("🔙 Back to Main Menu");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke Menu Utama: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
