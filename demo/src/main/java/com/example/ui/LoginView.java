package com.example.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.example.ui.components.CoTaButton;
import com.example.ui.components.CoTaTextField;
import com.example.ui.components.CoTaPasswordField;
import com.example.ui.components.Theme;

public class LoginView extends VBox {

    // Olay Dinleyicileri
    private Runnable onSignInClick;
    private Runnable onRegisterClick;

    // Alanları sınıf seviyesine çıkardık (Hata buradaydı!)
    private CoTaTextField idField;
    private CoTaPasswordField passField;

    public LoginView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        Label appName = new Label("COTAMAN");
        appName.setFont(Theme.getHeaderFont());
        appName.setStyle("-fx-text-fill: " + Theme.SECONDARY_COLOR + "; -fx-font-size: 32px; -fx-font-weight: bold;");

        VBox loginCard = new VBox(20);
        loginCard.setMaxWidth(350);
        loginCard.setAlignment(Pos.CENTER_LEFT);
        loginCard.setPadding(new javafx.geometry.Insets(30));

        loginCard.setStyle(
                "-fx-background-color: " + Theme.PANEL_COLOR1 + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label titleLabel = new Label("Sign In");
        titleLabel.setFont(Theme.getHeaderFont());
        titleLabel.setStyle("-fx-text-fill: " + Theme.TEXT_WHITE + ";");

        Label subTitle = new Label("Sign in to your account");
        subTitle.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-font-size: 14px;");

        // Alanları BAŞLATIYORUZ (Constructor içinde)
        idField = new CoTaTextField("Bilkent Webmail / ID");
        passField = new CoTaPasswordField("Password");

        CoTaButton signInBtn = new CoTaButton("Sign In", CoTaButton.StyleType.SECONDARY);
        signInBtn.setMaxWidth(Double.MAX_VALUE);

        // Butona basınca MainApp'e haber ver
        signInBtn.setOnAction(e -> {
            if (onSignInClick != null)
                onSignInClick.run();
        });

        Label forgotPass = new Label("Forgot Password?");
        forgotPass.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-underline: true;");
        forgotPass.setCursor(Cursor.HAND);
        forgotPass.setAlignment(Pos.CENTER_RIGHT);

        // Hizalama için container
        HBox forgotBox = new HBox(forgotPass);
        forgotBox.setAlignment(Pos.CENTER_RIGHT);

        loginCard.getChildren().addAll(titleLabel, subTitle, idField, passField, forgotBox, signInBtn);

        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + ";");

        Label registerLink = new Label("Sign Up");
        registerLink.setStyle("-fx-text-fill: " + Theme.PRIMARY_COLOR + "; -fx-font-weight: bold;");
        registerLink.setCursor(Cursor.HAND);

        registerLink.setOnMouseClicked(e -> {
            if (onRegisterClick != null)
                onRegisterClick.run();
        });

        HBox footerBox = new HBox(5, noAccountLabel, registerLink);
        footerBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(appName, loginCard, footerBox);
    }

    // --- GETTER METOTLARI ---
    // Artık idField null olmadığı için hata vermeyecek
    public String getEmail() {
        return idField.getText();
    }

    public String getPassword() {
        return passField.getText();
    }

    // --- SETTER METOTLARI ---
    public void setOnSignInClick(Runnable action) {
        this.onSignInClick = action;
    }

    public void setOnRegisterClick(Runnable action) {
        this.onRegisterClick = action;
    }
}
