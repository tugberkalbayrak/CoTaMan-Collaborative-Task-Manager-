package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showWelcome();
    }

    public static void showWelcome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("COTAMAN");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("COTAMAN - Sign In");
        primaryStage.setScene(scene);
    }

    public static void showRegister() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("register.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("COTAMAN - Sign Up");
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}