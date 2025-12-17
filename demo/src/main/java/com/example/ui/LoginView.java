package com.example.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import com.example.ui.components.CoTaButton;
import com.example.ui.components.CoTaTextField;
import com.example.ui.components.Theme;
import com.example.ui.components.CoTaPasswordField;

public class LoginView extends VBox {
    private Runnable onRegisterClick;
    private Runnable onSignInClick;

    public LoginView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        Label appName = new Label("COTAMAN");
        appName.setFont(Theme.getHeaderFont());
        appName.setStyle("-fx-text-fill: " + Theme.SECONDARY_COLOR + "; -fx-font-size: 40px; -fx-font-weight: bold;");

        VBox loginCard = new VBox(15);
        loginCard.setMaxWidth(350);
        loginCard.setAlignment(Pos.CENTER_LEFT);
        loginCard.setPadding(new javafx.geometry.Insets(30));

        loginCard.setStyle(
            "-fx-background-color: " + Theme.PANEL_COLOR1 + ";" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);"
        );

        Label titleLabel = new Label("Sign In");
        titleLabel.setFont(Theme.getHeaderFont());
        titleLabel.setStyle("-fx-text-fill: " + Theme.TEXT_WHITE + ";");

        CoTaTextField idField = new CoTaTextField("Bilkent ID");
       CoTaPasswordField passField = new CoTaPasswordField("Password");

        Label forgotPassLabel = new Label("Forgot Password?");
        forgotPassLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-font-size: 12px; -fx-underline: true;");
        forgotPassLabel.setCursor(Cursor.HAND);
        HBox forgotPassBox = new HBox(forgotPassLabel);
        forgotPassBox.setAlignment(Pos.CENTER_RIGHT);

        CoTaButton signInBtn = new CoTaButton("Sign In", CoTaButton.StyleType.PRIMARY);
        signInBtn.setMaxWidth(Double.MAX_VALUE);

        signInBtn.setOnAction(e -> {
            if (onSignInClick != null) onSignInClick.run();
        });
        loginCard.getChildren().addAll(titleLabel, idField, passField, forgotPassBox, signInBtn);

        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + ";");
        
        Label registerLink = new Label("Register here");
        registerLink.setStyle("-fx-text-fill: " + Theme.SECONDARY_COLOR + "; -fx-font-weight: bold;");
        registerLink.setCursor(Cursor.HAND);

        registerLink.setOnMouseClicked(e -> {
            if (onRegisterClick != null) onRegisterClick.run();
        });
        
        HBox footerBox = new HBox(5, noAccountLabel, registerLink);
        footerBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(appName, loginCard, footerBox);
    }

    public void setOnRegisterClick(Runnable action) {
        this.onRegisterClick = action;
    }

    public void setOnSignInClick(Runnable action) {
        this.onSignInClick = action;
    }
}
