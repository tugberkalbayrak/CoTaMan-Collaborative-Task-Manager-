package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddFriendPopup extends VBox {
    private Runnable onCancel;
    private java.util.function.Consumer<String> onAdd;
    private CoTaTextField emailField;
    private Label statusLabel;

    public AddFriendPopup() {
        this.setMaxWidth(350);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle(
                "-fx-background-color: #3C3C3C; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label("Add New Friend");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        emailField = new CoTaTextField("Friend's Email");
        statusLabel = new Label("");

        CoTaButton addBtn = new CoTaButton("Add Friend", CoTaButton.StyleType.PRIMARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);

        HBox actionBox = new HBox(10, cancelBtn, addBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });
        addBtn.setOnAction(e -> {
            if (onAdd != null && !emailField.getText().isEmpty())
                onAdd.accept(emailField.getText());
        });

        this.getChildren().addAll(title, emailField, statusLabel, actionBox);
    }

    public void setStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: " + (success ? "#27AE60" : "#C0392B") + ";");
    }

    public void setOnCancel(Runnable r) {
        this.onCancel = r;
    }

    public void setOnAdd(java.util.function.Consumer<String> c) {
        this.onAdd = c;
    }
}