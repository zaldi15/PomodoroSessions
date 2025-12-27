package com.aplikasi.view;

import com.aplikasi.dao.MissionsDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

public class AddMissionController {

    @FXML private TextField txtTitle;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpTargetDate;
    @FXML private Button btnSave;

    private final MissionsDAO missionsDAO = new MissionsDAO();
    private boolean isSaveClicked = false;

    public boolean isSaveClicked() {
        return isSaveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            String title = txtTitle.getText();
            String desc = txtDescription.getText();
            // Mengubah format tanggal dari DatePicker ke String (YYYY-MM-DD)
            String date = dpTargetDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            boolean success = missionsDAO.insertMission(title, desc, date);
            
            if (success) {
                isSaveClicked = true;
                closeWindow();
            } else {
                showAlert("Database Error", "Gagal menyimpan ke database.");
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean isInputValid() {
        if (txtTitle.getText().isEmpty() || dpTargetDate.getValue() == null) {
            showAlert("Input Invalid", "Title dan Target Date wajib diisi!");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}