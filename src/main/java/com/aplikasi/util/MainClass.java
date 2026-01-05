package com.aplikasi.util;

import com.aplikasi.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClass extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) {
        setMainStage(primaryStage);
        primaryStage.setTitle("Aplikasi Timer");

        // ⬇️ Start dari halaman LOGIN (bukan register lagi)
        openLoginPage();
       
    }

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }
    
    
    
    public static void openAdminDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com/aplikasi/view/AdminDashboard.fxml"));
            Parent root = loader.load();

           

            mainStage.setScene(new Scene(root));
            mainStage.setTitle("Pomodoro - Admin Panel [" + user.getUsername() + "]");
            mainStage.show();

            System.out.println("✅ AdminDashboard.fxml loaded for Admin: " + user.getUsername());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error loading AdminDashboard.fxml - Pastikan file FXML sudah dibuat");
        }
    }

   
    public static void openMenuUtama(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com/aplikasi/view/MenuUtama.fxml"));
            Parent root = loader.load();

            // inject user ke controller menu utama jika perlu
            Object controller = loader.getController();
            if (controller instanceof com.aplikasi.view.MenuController) {
                ((com.aplikasi.view.MenuController) controller).initForUser(user);
            }

            mainStage.setScene(new Scene(root));
            mainStage.show();

            System.out.println("✅ MenuUtama.fxml loaded");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error loading MenuUtama.fxml");
        }
    }

    // ✅ membuka halaman LOGIN pertama kali
    public static void openLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com/aplikasi/view/Login.fxml"));
            Parent root = loader.load();
            mainStage.setScene(new Scene(root));
            mainStage.show();

            System.out.println("✅ Login.fxml loaded");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error loading Login.fxml");
        }
    }

 
    public static void openRegisterPage() {
        try {
            FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com/aplikasi/view/Register.fxml"));
            Parent root = loader.load();
            mainStage.setScene(new Scene(root));
            mainStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
