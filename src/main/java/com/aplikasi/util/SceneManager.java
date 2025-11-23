package com.aplikasi.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    // Metode untuk mengganti scene/tampilan pada Stage yang sama
    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        // Ganti Scene di Stage yang sama
        stage.setScene(new Scene(root));
        stage.show();
    }
}