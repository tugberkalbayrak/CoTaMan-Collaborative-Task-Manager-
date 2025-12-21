package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddTaskPopup extends VBox {

    private Runnable onCancel;
    private java.util.function.BiConsumer<String, String> onAdd; // İsim ve Tarih döner

    private CoTaTextField taskNameField;
    private CoTaTextField dateField;

    public AddTaskPopup() {
        this.setMaxWidth(350);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle(
                "-fx-background-color: #3C3C3C; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label("New Task");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        taskNameField = new CoTaTextField("Task Description");
        dateField = new CoTaTextField("Due Date (e.g. 25 Dec)");

        CoTaButton addBtn = new CoTaButton("Add", CoTaButton.StyleType.PRIMARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);

        HBox actionBox = new HBox(10, cancelBtn, addBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });
        addBtn.setOnAction(e -> {
            if (onAdd != null && !taskNameField.getText().isEmpty()) {
                onAdd.accept(taskNameField.getText(), dateField.getText());
            }
        });

        this.getChildren().addAll(title, taskNameField, dateField, actionBox);
    }

    public void setOnCancel(Runnable action) {
        this.onCancel = action;
    }

    public void setOnAdd(java.util.function.BiConsumer<String, String> action) {
        this.onAdd = action;
    }
}