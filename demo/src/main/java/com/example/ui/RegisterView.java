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
    private Runnable onLoginClick;

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
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);"
        );

        Label titleLabel = new Label("Sign Up");
        titleLabel.setFont(Theme.getHeaderFont());
        titleLabel.setStyle("-fx-text-fill: " + Theme.TEXT_WHITE + ";");

        CoTaTextField nameField = new CoTaTextField("Name");
        CoTaTextField surnameField = new CoTaTextField("Surname");
        nameField.setPrefWidth(170);
        surnameField.setPrefWidth(170);
        
        HBox nameBox = new HBox(10, nameField, surnameField);

        CoTaTextField idField = new CoTaTextField("Bilkent ID");
        CoTaTextField emailField = new CoTaTextField("Bilkent Webmail");
        CoTaPasswordField passField = new CoTaPasswordField("Password");
        CoTaPasswordField rePassField = new CoTaPasswordField("Password Again");

        CoTaButton registerBtn = new CoTaButton("Register", CoTaButton.StyleType.SECONDARY);
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        registerCard.getChildren().addAll(
            titleLabel, 
            nameBox, 
            idField, 
            emailField, 
            passField, 
            rePassField, 
            registerBtn
        );

        Label hasAccountLabel = new Label("Already have an account?");
        hasAccountLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + ";");
        
        Label loginLink = new Label("Sign In");
        loginLink.setStyle("-fx-text-fill: " + Theme.PRIMARY_COLOR + "; -fx-font-weight: bold;");
        loginLink.setCursor(Cursor.HAND);

        loginLink.setOnMouseClicked(e -> {
            if (onLoginClick != null) onLoginClick.run();
        });
        
        HBox footerBox = new HBox(5, hasAccountLabel, loginLink);
        footerBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(appName, registerCard, footerBox);
    }

    public void setOnLoginClick(Runnable action) {
        this.onLoginClick = action;
    }
}