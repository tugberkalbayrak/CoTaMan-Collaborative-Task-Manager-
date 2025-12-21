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

public class MainApp extends Application {

    private StackPane root;
    private LoginView loginView;
    private RegisterView registerView;
    private MainView mainView;
    private ArchiveView archiveView;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();

        // Giriş ve Kayıt ekranlarını oluştur
        loginView = new LoginView();
        registerView = new RegisterView();

        // --- 1. EKRAN GEÇİŞLERİ (LOGIN <-> REGISTER) ---
        loginView.setOnRegisterClick(() -> showScreen(registerView));
        registerView.setOnLoginClick(() -> showScreen(loginView));

        // --- 2. KAYIT İŞLEMİ ---
        registerView.setOnRegisterClick(() -> {
            String name = registerView.getName();
            String surname = registerView.getSurname();
            String email = registerView.getEmail();
            String pass = registerView.getPassword();
            String rePass = registerView.getRePassword();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                System.out.println("UYARI: Lütfen tüm alanları doldurun!");
                return;
            }

            if (!pass.equals(rePass)) {
                System.out.println("UYARI: Şifreler uyuşmuyor!");
                return;
            }

            System.out.println("Kayıt işlemi başlatılıyor: " + email);
            boolean success = SessionManager.getInstance().register(name + " " + surname, email, pass);

            if (success) {
                System.out.println("✅ KAYIT BAŞARILI! Giriş ekranına yönlendiriliyorsunuz.");
                showScreen(loginView);
            } else {
                System.out.println("❌ KAYIT BAŞARISIZ! Bu email zaten kullanılıyor olabilir.");
            }
        });

        // --- 3. GİRİŞ İŞLEMİ ---
        loginView.setOnSignInClick(() -> {
            String email = loginView.getEmail();
            String pass = loginView.getPassword();

            System.out.println("Giriş deneniyor...");
            boolean success = SessionManager.getInstance().login(email, pass);

            if (success) {
                System.out.println("✅ GİRİŞ BAŞARILI! Ana sayfa yükleniyor...");

                // Giriş başarılı olunca Ana Ekranları oluştur
                initializeLoggedInViews();

                // Ana Ekrana Geç
                showScreen(mainView);

                primaryStage.setWidth(1200);
                primaryStage.setHeight(800);
                primaryStage.centerOnScreen();
            } else {
                System.out.println("❌ GİRİŞ BAŞARISIZ! Email veya şifre hatalı.");
            }
        });

        // Başlangıç ekranı
        showScreen(loginView);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("CoTaMan - Collaborative Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- GİRİŞ SONRASI KURULUM (DÜZELTİLDİ) ---
    private void initializeLoggedInViews() {
        mainView = new MainView();

        // Sadece ArchiveView geçişini burada tanımlıyoruz.
        // GroupView geçişlerini artık MainView kendi içinde hallediyor!

        archiveView = new ArchiveView();

        // MainView -> ArchiveView Geçişi
        mainView.getNavBar().setOnArchiveClick(() -> showScreen(archiveView));

        // ArchiveView -> MainView (Geri Dönüş)
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