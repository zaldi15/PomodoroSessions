package com.aplikasi.view;

import com.aplikasi.dao.MissionsDAO;
import com.aplikasi.dao.UserDAO;
import com.aplikasi.dao.SessionDAO;
import com.aplikasi.model.Missions;
import com.aplikasi.model.User;
import com.aplikasi.util.MainClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminController {

    // --- TABEL MISI ---
    @FXML private TableView<Missions> tvMissions;
    @FXML private TableColumn<Missions, Integer> colId;
    @FXML private TableColumn<Missions, String> colTitle;
    @FXML private TableColumn<Missions, String> colDescription;
    @FXML private TableColumn<Missions, String> colTargetDate;

    // --- TABEL USER ---
    @FXML private TableView<User> tvUsers;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, LocalDateTime> colLastLogin;

    // --- ANALYTICS (BARU) ---
    @FXML private VBox vbAnalytics;
    @FXML private PieChart categoryChart;

    // --- UI CONTROLS & SIDEBAR ---
    @FXML private Label lblHeaderTitle;
    @FXML private Label lblContentSubtitle;
    @FXML private HBox hbMissionActions;
    @FXML private HBox hbUserActions;
    @FXML private Button btnManageMissions;
    @FXML private Button btnManageUsers;
    @FXML private Button btnAnalytics;
    @FXML private Button btnAddMission;

    private final MissionsDAO missionsDAO = new MissionsDAO();
    private final UserDAO userDAO = new UserDAO();

    // --- STYLE KONSTANTA ---
    private final String ACTIVE_STYLE = "-fx-background-color: #021526; -fx-border-color: #6EACDA; -fx-border-width: 0 0 0 4; -fx-background-radius: 8;";
    private final String INACTIVE_STYLE = "-fx-background-color: transparent; -fx-border-width: 0;";

    @FXML
    public void initialize() {
        // 1. Setup Kolom Misi
        colId.setCellValueFactory(new PropertyValueFactory<>("idMission"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTargetDate.setCellValueFactory(new PropertyValueFactory<>("targetDate"));

        // 2. Setup Kolom User
        colUserId.setCellValueFactory(new PropertyValueFactory<>("user_id")); // Pastikan sesuai nama field di Model User
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colLastLogin.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));

        // 3. Default View
        handleShowMissions();
    }

    // --- LOGIKA PERPINDAHAN TAMPILAN (VIEW SWITCHER) ---

    @FXML
    private void handleShowMissions() {
        updateSidebarUI(btnManageMissions);
        setSectionVisible(true, false, false);
        lblHeaderTitle.setText("Mission Control Center");
        lblContentSubtitle.setText("🎯 Global Daily Missions");
        loadMissionsData();
    }

    @FXML
    private void handleShowUsers() {
        updateSidebarUI(btnManageUsers);
        setSectionVisible(false, true, false);
        lblHeaderTitle.setText("User Management");
        lblContentSubtitle.setText("👥 Registered Users List");
        loadUsersData();
    }

    @FXML
    private void handleShowAnalytics() {
        updateSidebarUI(btnAnalytics);
        setSectionVisible(false, false, true);
        lblHeaderTitle.setText("Platform Analytics");
        lblContentSubtitle.setText("📊 Global Productivity Stats");
        loadAnalyticsData();
    }

    /**
     * Mengatur visibilitas elemen berdasarkan menu yang dipilih
     */
    private void setSectionVisible(boolean showMissions, boolean showUsers, boolean showAnalytics) {
        // Tabel Misi
        tvMissions.setVisible(showMissions);
        tvMissions.setManaged(showMissions);
        hbMissionActions.setVisible(showMissions);
        hbMissionActions.setManaged(showMissions);

        // Tabel User
        tvUsers.setVisible(showUsers);
        tvUsers.setManaged(showUsers);
        hbUserActions.setVisible(showUsers);
        hbUserActions.setManaged(showUsers);

        // Analytics
        vbAnalytics.setVisible(showAnalytics);
        vbAnalytics.setManaged(showAnalytics);
    }

    private void updateSidebarUI(Button activeButton) {
        btnManageMissions.setStyle(INACTIVE_STYLE);
        btnManageUsers.setStyle(INACTIVE_STYLE);
        btnAnalytics.setStyle(INACTIVE_STYLE);
        activeButton.setStyle(ACTIVE_STYLE);
    }

    // --- LOGIKA DATA ---

    private void loadMissionsData() {
        tvMissions.setItems(FXCollections.observableArrayList(missionsDAO.getAllMissions()));
    }

    private void loadUsersData() {
        tvUsers.setItems(FXCollections.observableArrayList(userDAO.getAllUsers()));
    }

    private void loadAnalyticsData() {
        // Mengambil data statistik kategori dari SessionDAO
        Map<String, Double> stats = SessionDAO.getGlobalCategoryStats();
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        if (stats.isEmpty()) {
            categoryChart.setTitle("Belum ada data sesi Pomodoro");
        } else {
            categoryChart.setTitle("Distribusi Fokus Berdasarkan Kategori");
            stats.forEach((category, totalMinutes) -> {
                double hours = totalMinutes / 60.0;
                pieChartData.add(new PieChart.Data(category + " (" + String.format("%.1f", hours) + " jam)", totalMinutes));
            });
        }
        categoryChart.setData(pieChartData);
    }

    // --- ACTION HANDLERS ---

    @FXML
    private void handleAddMission() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/AddMission.fxml"));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Mission");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(lblHeaderTitle.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            AddMissionController controller = loader.getController();
            dialogStage.showAndWait();

            if (controller.isSaveClicked()) loadMissionsData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka form: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteMission() {
        Missions selected = tvMissions.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDialog("Hapus Misi", "Hapus misi: " + selected.getTitle() + "?")) {
            if (missionsDAO.deleteMission(selected.getIdMission())) {
                loadMissionsData();
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Misi dihapus.");
            }
        } else if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih misi terlebih dahulu.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = tvUsers.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDialog("Ban User", "Hapus user " + selected.getUsername() + "?")) {
            // Note: Pastikan model User memiliki getId() yang mengacu ke user_id
            if (userDAO.deleteUser(selected.getUser_id())) {
                loadUsersData();
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "User dihapus.");
            }
        } else if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih user terlebih dahulu.");
        }
    }

    @FXML
    private void handleLogout() {
        MainClass.openLoginPage();
    }

    // --- HELPER ---

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private boolean confirmDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}