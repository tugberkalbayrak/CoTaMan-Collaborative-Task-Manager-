package com.example.ui.components;

import com.example.Entity.Importance;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;

public class AddEventPopup extends VBox {

    private Runnable onCancel;
    private Runnable onSave;

    private CoTaTextField nameField;
    private ComboBox<String> dayCombo;
    private ComboBox<String> timeCombo;

    private Importance selectedImportance = Importance.MUST;
    private Map<Importance, CoTaButton> importanceButtons = new HashMap<>();

    public AddEventPopup() {

        this.setMaxWidth(400);
        this.setMaxHeight(450);
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        this.setStyle(
                "-fx-background-color: #3C3C3C;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

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
        // importanceBox.setAlignment(Pos.CENTER_LEFT);

        importanceBox.getChildren().add(createImportanceBtn("Critical", Importance.CRITICAL, "#C0392B"));
        importanceBox.getChildren().add(createImportanceBtn("Must", Importance.MUST, "#E67E22"));
        importanceBox.getChildren().add(createImportanceBtn("Trivia", Importance.TRIVIA, "#27AE60"));

        updateButtonStyles(); // Set initial selection style

        CoTaButton saveBtn = new CoTaButton("Save", CoTaButton.StyleType.SECONDARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);

        saveBtn.setPrefWidth(100);
        cancelBtn.setPrefWidth(100);

        HBox actionBox = new HBox(20, saveBtn, cancelBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });

        saveBtn.setOnAction(e -> {
            if (onSave != null)
                onSave.run();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(
                title,
                nameField,
                dateBox,
                importanceLabel, importanceBox,
                spacer,
                actionBox);
    }

    private void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
                "-fx-background-color: #2B2B2B; " +
                        "-fx-text-base-color: white; " +
                        "-fx-prompt-text-fill: white; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-font-size: 14px;");
        combo.setPrefWidth(150);
    }

    private CoTaButton createImportanceBtn(String text, Importance importance, String color) {
        CoTaButton btn = new CoTaButton(text, CoTaButton.StyleType.SECONDARY);
        // Store color in a simple way or just reuse strict styling
        btn.getProperties().put("baseColor", color);
        btn.setPrefWidth(80); // Increased width to fit text

        // Override default CoTaButton hover behavior
        btn.setOnMouseEntered(e -> {
            String style = "-fx-background-color: " + color
                    + "; -fx-text-fill: white; -fx-background-radius: 15; -fx-opacity: 0.8;";
            if (this.selectedImportance == importance) {
                style += " -fx-border-color: white; -fx-border-width: 2;";
            }
            btn.setStyle(style);
        });

        btn.setOnMouseExited(e -> {
            updateButtonStyles(); // Restore correct state
        });

        btn.setOnAction(e -> {
            this.selectedImportance = importance;
            updateButtonStyles();
        });

        importanceButtons.put(importance, btn);
        return btn;
    }

    private void updateButtonStyles() {
        for (Map.Entry<Importance, CoTaButton> entry : importanceButtons.entrySet()) {
            Importance imp = entry.getKey();
            CoTaButton btn = entry.getValue();
            String color = (String) btn.getProperties().get("baseColor");

            if (imp == this.selectedImportance) {
                // Selected style: Brighter border, maybe glow
                btn.setStyle("-fx-background-color: " + color
                        + "; -fx-text-fill: white; -fx-background-radius: 15; -fx-border-color: white; -fx-border-width: 2;");
            } else {
                // Unselected: Normal
                btn.setStyle("-fx-background-color: " + color
                        + "; -fx-text-fill: rgba(255,255,255,0.7); -fx-background-radius: 15; -fx-border-width: 0;");
            }
        }
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

    public Importance getSelectedImportance() {
        return selectedImportance;
    }

    public String getSelectedColor() {
        // Fallback or helper if needed elsewhere
        switch (selectedImportance) {
            case CRITICAL:
                return "#C0392B";
            case MUST:
                return "#E67E22";
            case TRIVIA:
                return "#27AE60";
            default:
                return "#8E44AD";
        }
    }

    public void setOnCancel(Runnable action) {
        this.onCancel = action;
    }

    public void setOnSave(Runnable action) {
        this.onSave = action;
    }
}