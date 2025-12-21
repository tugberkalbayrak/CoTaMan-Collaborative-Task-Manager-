package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AddEventPopup extends VBox {

    private Runnable onCancel;
    private Runnable onSave;

    private CoTaTextField nameField;
    private ComboBox<String> dayCombo;
    private ComboBox<String> timeCombo;

    private String selectedImportanceColor = "#8E44AD"; 

    public AddEventPopup() {

        this.setMaxWidth(400);
        this.setMaxHeight(450);
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);
        
        this.setStyle(
            "-fx-background-color: #3C3C3C;" + 
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);"
        );

        Label title = new Label("Add Event");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        nameField = new CoTaTextField("Event Name (e.g. Project)");

        dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        dayCombo.setPromptText("Select Day");
        styleComboBox(dayCombo);

        timeCombo = new ComboBox<>();
        timeCombo.getItems().addAll("8:30", "9:30", "10:30", "11:30", "12:30", "13:30", "14:30", "15:30", "16:30");
        timeCombo.setPromptText("Start Time");
        styleComboBox(timeCombo);

        HBox dateBox = new HBox(15, dayCombo, timeCombo);

        Label importanceLabel = new Label("Importance Rate:");
        importanceLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + ";");
        
        HBox importanceBox = new HBox(10);

        importanceBox.getChildren().add(createImportanceBtn("High", "#C0392B"));
        importanceBox.getChildren().add(createImportanceBtn("Mid", "#E67E22"));
        importanceBox.getChildren().add(createImportanceBtn("Low", "#27AE60"));

        CoTaButton saveBtn = new CoTaButton("Save", CoTaButton.StyleType.SECONDARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);
        
        saveBtn.setPrefWidth(100);
        cancelBtn.setPrefWidth(100);

        HBox actionBox = new HBox(20, saveBtn, cancelBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT); 

        cancelBtn.setOnAction(e -> {
            if (onCancel != null) onCancel.run();
        });
        
        saveBtn.setOnAction(e -> {
            if (onSave != null) onSave.run();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(
            title, 
            nameField, 
            dateBox, 
            importanceLabel, importanceBox, 
            spacer, 
            actionBox
        );
    }

    private void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
            "-fx-background-color: #2B2B2B; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;"
        );
        combo.setPrefWidth(150);
    }

    private CoTaButton createImportanceBtn(String text, String color) {
        CoTaButton btn = new CoTaButton(text, CoTaButton.StyleType.SECONDARY);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 15;");
        btn.setPrefWidth(60);

        btn.setOnAction(e -> {
            this.selectedImportanceColor = color;
            System.out.println("Renk seçildi: " + color); 
        });
        
        return btn;
    }

    public String getEventName() {
        return nameField.getText();
    }

    public int getDayIndex() {
        return dayCombo.getSelectionModel().getSelectedIndex();
    }

    public int getTimeIndex() {
        return timeCombo.getSelectionModel().getSelectedIndex();
    }

    public String getSelectedColor() {
        return selectedImportanceColor;
    }

    public void setOnCancel(Runnable action) { this.onCancel = action; }
    public void setOnSave(Runnable action) { this.onSave = action; }
}