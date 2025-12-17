package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.example.ui.MainView;
import com.example.ui.LoginView;
import com.example.ui.RegisterView;
import com.example.ui.ArchiveView;

public class MainApp extends Application {
    
    private StackPane root;
    private LoginView loginView;
    private RegisterView registerView;
    private MainView mainView;
    private ArchiveView archiveView;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();

        loginView = new LoginView();
        registerView = new RegisterView();
        mainView = new MainView();
        archiveView = new ArchiveView();

        loginView.setOnRegisterClick(() -> showScreen(registerView));
        registerView.setOnLoginClick(() -> showScreen(loginView));
        loginView.setOnSignInClick(() -> {
            showScreen(mainView);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.centerOnScreen();
        });

        mainView.getNavBar().setOnArchiveClick(() -> {
            System.out.println("Arşive gidiliyor...");
            showScreen(archiveView);
        });

        archiveView.getNavBar().setOnHomeClick(() -> {
            System.out.println("Ana sayfaya dönülüyor...");
            showScreen(mainView);
        });

        showScreen(loginView);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("CoTaMan - Collaborative Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showScreen(javafx.scene.Node screen) {
        root.getChildren().clear();
        root.getChildren().add(screen);
    }

    public static void main(String[] args) {
        launch(args);
    }
}