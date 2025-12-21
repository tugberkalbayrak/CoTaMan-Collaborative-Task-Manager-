package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SRSPopup extends VBox {

    private Runnable onCancel;
    private java.util.function.BiConsumer<String, String> onLoginRequest;
    private java.util.function.Consumer<String> onSmsVerify;

    private CoTaTextField idField;
    private CoTaPasswordField passField;
    private CoTaTextField smsField;

    private VBox inputContainer;
    private Label infoLabel;
    private CoTaButton actionBtn;

    public SRSPopup() {
        this.setMaxWidth(350);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle(
                "-fx-background-color: #2C3E50; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label("Sync with SRS");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        infoLabel = new Label("Enter your STARS credentials.");
        infoLabel.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px;");

        inputContainer = new VBox(10);

        idField = new CoTaTextField("Bilkent ID");
        passField = new CoTaPasswordField("SRS Password");
        inputContainer.getChildren().addAll(idField, passField);

        actionBtn = new CoTaButton("Send Code", CoTaButton.StyleType.SECONDARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);

        HBox actionBox = new HBox(10, cancelBtn, actionBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });
        actionBtn.setOnAction(e -> handleAction());

        this.getChildren().addAll(title, infoLabel, inputContainer, actionBox);
    }

    private void handleAction() {
        if (smsField == null) {
            if (onLoginRequest != null)
                onLoginRequest.accept(idField.getText(), passField.getText());
        } else {
            if (onSmsVerify != null)
                onSmsVerify.accept(smsField.getText());
        }
    }

    public void switchToSmsMode() {
        inputContainer.getChildren().clear();
        smsField = new CoTaTextField("SMS Verification Code");
        inputContainer.getChildren().add(smsField);
        infoLabel.setText("SMS sent! Please enter the code.");
        actionBtn.setText("Verify & Sync");
    }

    public void setOnCancel(Runnable action) {
        this.onCancel = action;
    }

    public void setOnLoginRequest(java.util.function.BiConsumer<String, String> action) {
        this.onLoginRequest = action;
    }

    public void setOnSmsVerify(java.util.function.Consumer<String> action) {
        this.onSmsVerify = action;
    }
}