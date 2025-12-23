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

public class RegisterView extends VBox {

    // Olay Dinleyicileri
    private Runnable onLoginClick;
    private Runnable onRegisterClick;

    // Veri Alanları
    private CoTaTextField nameField;
    private CoTaTextField surnameField;
    private CoTaTextField idField;
    private CoTaTextField emailField;
    private CoTaPasswordField passField;
    private CoTaPasswordField rePassField;

    public RegisterView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        Label appName = new Label("COTAMAN");
        appName.setFont(Theme.getHeaderFont());
        appName.setStyle("-fx-text-fill: " + Theme.SECONDARY_COLOR + "; -fx-font-size: 32px; -fx-font-weight: bold;");

        VBox registerCard = new VBox(10);
        registerCard.setMaxWidth(400);
        registerCard.setAlignment(Pos.CENTER_LEFT);
        registerCard.setPadding(new javafx.geometry.Insets(25));

        registerCard.setStyle(
                "-fx-background-color: " + Theme.PANEL_COLOR1 + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label titleLabel = new Label("Sign Up");
        titleLabel.setFont(Theme.getHeaderFont());
        titleLabel.setStyle("-fx-text-fill: " + Theme.TEXT_WHITE + ";");

        // Alanları Oluştur
        nameField = new CoTaTextField("Name");
        surnameField = new CoTaTextField("Surname");
        nameField.setPrefWidth(170);
        surnameField.setPrefWidth(170);

        HBox nameBox = new HBox(10, nameField, surnameField);

        idField = new CoTaTextField("Bilkent ID");
        emailField = new CoTaTextField("Bilkent Webmail");
        passField = new CoTaPasswordField("Password");
        rePassField = new CoTaPasswordField("Password Again");

        CoTaButton registerBtn = new CoTaButton("Register", CoTaButton.StyleType.SECONDARY);
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        // Register butonuna tıklama olayı
        registerBtn.setOnAction(e -> {
            if (onRegisterClick != null)
                onRegisterClick.run();
        });

        registerCard.getChildren().addAll(
                titleLabel,
                nameBox,
                idField,
                emailField,
                passField,
                rePassField,
                registerBtn);

        Label hasAccountLabel = new Label("Already have an account?");
        hasAccountLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + ";");

        Label loginLink = new Label("Sign In");
        loginLink.setStyle("-fx-text-fill: " + Theme.PRIMARY_COLOR + "; -fx-font-weight: bold;");
        loginLink.setCursor(Cursor.HAND);

        loginLink.setOnMouseClicked(e -> {
            if (onLoginClick != null)
                onLoginClick.run();
        });

        HBox footerBox = new HBox(5, hasAccountLabel, loginLink);
        footerBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(appName, registerCard, footerBox);
    }

    // --- GETTER METOTLARI ---
    public String getName() {
        return nameField.getText();
    }

    public String getSurname() {
        return surnameField.getText();
    }

    public String getBilkentId() {
        return idField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return passField.getText();
    }

    public String getRePassword() {
        return rePassField.getText();
    }

    // --- SETTER METOTLARI ---
    public void setOnLoginClick(Runnable action) {
        this.onLoginClick = action;
    }

    public void setOnRegisterClick(Runnable action) {
        this.onRegisterClick = action;
    }
}