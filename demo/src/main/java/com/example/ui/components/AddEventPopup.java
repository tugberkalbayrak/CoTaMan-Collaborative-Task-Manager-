
package com.example.ui.components;

import com.example.Entity.Importance;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalTime;

public class AddEventPopup extends VBox {

    private Runnable onCancel;
    private Runnable onSave;

    private CoTaTextField nameField;
    private ComboBox<String> daySelect;
    private ComboBox<String> startTimeSelect;
    private ComboBox<String> endTimeSelect;

    private Importance selectedImportance = Importance.MUST;
    private HBox importanceBox;

    public AddEventPopup() {
        this.setMaxWidth(350);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER_LEFT);

        this.setStyle(
                "-fx-background-color: #3C3C3C; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label("Add New Event");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        nameField = new CoTaTextField("Event Name");

daySelect = new ComboBox<>();
        daySelect.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        daySelect.setPromptText("Select Day");
        daySelect.setMaxWidth(Double.MAX_VALUE);
        daySelect.setStyle("-fx-font-size: 12px;");

HBox timeBox = new HBox(10);
        startTimeSelect = new ComboBox<>();
        startTimeSelect.setPromptText("Start");
        startTimeSelect.setPrefWidth(150);

        endTimeSelect = new ComboBox<>();
        endTimeSelect.setPromptText("End");
        endTimeSelect.setPrefWidth(150);

        for (int h = 6; h < 24; h++) {
            String time = String.format("%02d:00", h);
            String timeHalf = String.format("%02d:30", h);
            startTimeSelect.getItems().addAll(time, timeHalf);
            endTimeSelect.getItems().addAll(time, timeHalf);
        }

        startTimeSelect.setOnAction(e -> {
            if (startTimeSelect.getValue() != null) {
                String start = startTimeSelect.getValue();
                int h = Integer.parseInt(start.split(":")[0]);
                int m = Integer.parseInt(start.split(":")[1]);
                LocalTime s = LocalTime.of(h, m);
                LocalTime end = s.plusHours(1);
                endTimeSelect.setValue(end.toString());
            }
        });

        timeBox.getChildren().addAll(startTimeSelect, endTimeSelect);

Label impLabel = new Label("Priority:");
        impLabel.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px;");

        importanceBox = new HBox(10);
        importanceBox.setAlignment(Pos.CENTER);

        for (Importance imp : Importance.values()) {
            Button btn = createImportanceButton(imp);
            importanceBox.getChildren().add(btn);
        }

if (!importanceBox.getChildren().isEmpty()) {
            updateSelectionStyles((Button) importanceBox.getChildren().get(0));
        }

HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        CoTaButton saveBtn = new CoTaButton("Save", CoTaButton.StyleType.PRIMARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);

        saveBtn.setOnAction(e -> {
            if (onSave != null)
                onSave.run();
        });
        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });

        actionBox.getChildren().addAll(cancelBtn, saveBtn);

        this.getChildren().addAll(title, nameField, daySelect, timeBox, impLabel, importanceBox, actionBox);
    }

    private Button createImportanceButton(Importance imp) {
        Button btn = new Button(imp.name());
        btn.setPrefWidth(90);
        String color = getImportanceColor(imp);
        btn.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");

        btn.setOnAction(e -> {
            this.selectedImportance = imp;
            updateSelectionStyles(btn);
        });

        return btn;
    }

    private void updateSelectionStyles(Button selectedBtn) {
        for (javafx.scene.Node node : importanceBox.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Importance imp = Importance.valueOf(btn.getText());
                String color = getImportanceColor(imp);

                if (btn == selectedBtn) {
                     
                    btn.setStyle("-fx-background-color: " + color
                            + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10;");
                } else {
                     
                    btn.setStyle("-fx-background-color: " + color
                            + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-border-width: 0;");
                }
            }
        }
    }

private String getImportanceColor(Importance imp) {
        if (imp == null)
            return "#95A5A6";
        switch (imp) {
            case MUST:
                return "#C0392B";  
            case OPTIONAL:
                return "#E67E22";  
            case TRIVIA:
                return "#27AE60";  
            default:
                return "#3498DB";
        }
    }

    public String getEventName() {
        return nameField.getText();
    }

    public int getDayIndex() {
        return daySelect.getSelectionModel().getSelectedIndex();
    }

    public String getStartTime() {
        return startTimeSelect.getValue();
    }

    public String getEndTime() {
        return endTimeSelect.getValue();
    }

    public Importance getSelectedImportance() {
        return selectedImportance;
    }

    public void setOnCancel(Runnable action) {
        this.onCancel = action;
    }

    public void setOnSave(Runnable action) {
        this.onSave = action;
    }
}