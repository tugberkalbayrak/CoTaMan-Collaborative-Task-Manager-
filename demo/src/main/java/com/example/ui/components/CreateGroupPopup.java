package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CreateGroupPopup extends VBox {

    private Runnable onCancel;
    // İsim ve Ders Kodu döner
    private java.util.function.BiConsumer<String, String> onCreate;

    private CoTaTextField nameField;
    private CoTaTextField courseField;

    public CreateGroupPopup() {
        this.setMaxWidth(350);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER_LEFT);

        this.setStyle(
                "-fx-background-color: #3C3C3C;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label("Create New Group");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        nameField = new CoTaTextField("Group Name (e.g. Study Bros)");
        courseField = new CoTaTextField("Course Code (e.g. CS102)");

        // Butonlar
        CoTaButton createBtn = new CoTaButton("Create", CoTaButton.StyleType.SECONDARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);

        HBox actionBox = new HBox(10, cancelBtn, createBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        // İptal Butonu
        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });

        // Oluştur Butonu
        createBtn.setOnAction(e -> {
            if (onCreate != null && !nameField.getText().isEmpty()) {
                onCreate.accept(nameField.getText(), courseField.getText());
            }
        });

        this.getChildren().addAll(title, nameField, courseField, actionBox);
    }

    public void setOnCancel(Runnable action) {
        this.onCancel = action;
    }

    public void setOnCreate(java.util.function.BiConsumer<String, String> action) {
        this.onCreate = action;
    }
}