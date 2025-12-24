package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.example.Manager.SessionManager;
import com.example.ui.LoginView;
import com.example.ui.RegisterView;
import com.example.ui.MainView;
import com.example.ui.ArchiveView;

import com.example.WebScraping.MoodleScraper;
import com.example.Entity.CalendarEvent;
import javafx.application.Platform;
import java.util.List;

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

        loginView.setOnRegisterClick(() -> showScreen(registerView));
        registerView.setOnLoginClick(() -> showScreen(loginView));

        registerView.setOnRegisterClick(() -> {
            String name = registerView.getName();
            String surname = registerView.getSurname();
            String email = registerView.getEmail();
            String pass = registerView.getPassword();
            String rePass = registerView.getRePassword();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                System.out.println("WARNING: Please fill in all fields!");
                return;
            }

            if (!pass.equals(rePass)) {
                System.out.println("WARNING: Passwords do not match!");
                return;
            }

            String moodleUser = registerView.getMoodleUsername();
            String moodlePass = registerView.getMoodlePassword();

            System.out.println("Registration starting: " + email);

            boolean success = SessionManager.getInstance().register(name + " " + surname, email, pass, moodleUser,
                    moodlePass);

            if (success) {
                System.out.println("REGISTRATION SUCCESSFUL! Redirecting to login screen.");
                showScreen(loginView);
            } else {
                System.out.println("REGISTRATION FAILED! Email might already be in use.");
            }
        });

        loginView.setOnSignInClick(() -> {
            String email = loginView.getEmail();
            String pass = loginView.getPassword();

            System.out.println("Attempting login...");
            boolean success = SessionManager.getInstance().login(email, pass);

            if (success) {
                System.out.println("LOGIN SUCCESSFUL! Loading main page...");

                initializeLoggedInViews();

                new Thread(() -> {
                    com.example.Entity.User currentUser = SessionManager.getInstance().getCurrentUser();
                    if (currentUser != null && currentUser.getMoodleUsername() != null
                            && !currentUser.getMoodleUsername().isEmpty()) {
                        System.out.println("Fetching Moodle data...");

                        MoodleScraper scraper = new MoodleScraper();
                        if (scraper.connect(currentUser.getMoodleUsername(), currentUser.getMoodlePassword())) {
                            List<CalendarEvent> events = scraper.fetchEvents();
                            System.out.println(events.size() + " events fetched from Moodle.");

                            for (CalendarEvent event : events) {
                                event.setOwner(currentUser);
                                SessionManager.getInstance().getRepository().saveEvent(event);
                            }

                            Platform.runLater(() -> {
                                if (mainView != null && mainView.getCalendarGrid() != null) {
                                    mainView.getCalendarGrid().loadEvents(SessionManager.getInstance().getUserEvents());
                                    System.out.println("Calendar updated!");
                                }
                            });
                        }
                    }
                }).start();

                showScreen(mainView);

                primaryStage.setWidth(1200);
                primaryStage.setHeight(800);
                primaryStage.centerOnScreen();
            } else {
                System.out.println("LOGIN FAILED! Incorrect email or password.");
            }
        });

        showScreen(loginView);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("CoTaMan - Collaborative Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeLoggedInViews() {
        mainView = new MainView();

        archiveView = new ArchiveView();

        mainView.getNavBar().setOnArchiveClick(() -> showScreen(archiveView));

        archiveView.getNavBar().setOnHomeClick(() -> showScreen(mainView));
    }

    private void showScreen(javafx.scene.Node screen) {
        root.getChildren().clear();
        root.getChildren().add(screen);
    }

    public static void main(String[] args) {
        launch(args);
    }
}