package com.aplikasi.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneManager {
    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.show();
    }


    public static <T> void switchSceneWithData(Stage stage, String fxmlPath, Consumer<T> controllerInitializer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Ambil controller
            T controller = loader.getController();

            // Eksekusi inisialisasi data
            if (controllerInitializer != null && controller != null) {
                controllerInitializer.accept(controller);
            }

            // Ganti scene
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
