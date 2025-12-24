package com.example.ui.components;

import com.example.Entity.TimeSlot;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MeetingSchedulerPopup extends VBox {

    private Runnable onCancel;
    private java.util.function.Consumer<String> onSave;

    private VBox slotsContainer;
    private ToggleGroup toggleGroup;
    private String finalSelectedRange = null;

    public MeetingSchedulerPopup(List<TimeSlot> availableSlots) {
        this.setMaxWidth(480);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER_LEFT);

        this.setStyle(
                "-fx-background-color: #BDC3C7; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label("Intelligent Meeting Scheduler");
        title.setFont(Theme.getHeaderFont());
        title.setStyle(
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #7F8C8D; -fx-padding: 10; -fx-background-radius: 15;");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);

        Label subTitle = new Label("Select a time range, start & end time:");
        subTitle.setStyle("-fx-text-fill: #2C3E50; -fx-font-size: 13px; -fx-font-weight: bold;");

        slotsContainer = new VBox(10);
        toggleGroup = new ToggleGroup();

        if (availableSlots.isEmpty()) {
            Label noSlot = new Label("No common time found for next 7 days!");
            noSlot.setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
            slotsContainer.getChildren().add(noSlot);
        } else {
            for (TimeSlot slot : availableSlots) {
                slotsContainer.getChildren().add(createSlotRow(slot));
            }
        }

        ScrollPane scroll = new ScrollPane(slotsContainer);
        scroll.setMaxHeight(300);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        CoTaButton saveBtn = new CoTaButton("Confirm Meeting", CoTaButton.StyleType.PRIMARY);
        CoTaButton cancelBtn = new CoTaButton("Cancel", CoTaButton.StyleType.DANGER);

        HBox actionBox = new HBox(10, cancelBtn, saveBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });

        saveBtn.setOnAction(e -> {
            if (onSave != null && finalSelectedRange != null) {
                onSave.accept(finalSelectedRange);
            }
        });

        this.getChildren().addAll(title, subTitle, scroll, actionBox);
    }

    private VBox createSlotRow(TimeSlot slot) {
        VBox container = new VBox(5);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #9B59B6; -fx-background-radius: 15;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd MMM (EEE)");
        DateTimeFormatter fmtTime = DateTimeFormatter.ofPattern("HH:mm");
        String rangeText = slot.getStart().format(fmtDate) + " | " +
                slot.getStart().format(fmtTime) + " - " +
                slot.getEnd().format(fmtTime);

        RadioButton rb = new RadioButton(rangeText);
        rb.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        rb.setToggleGroup(toggleGroup);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(rb, spacer);

        HBox selectors = new HBox(10);
        selectors.setAlignment(Pos.CENTER_LEFT);
        selectors.setVisible(false);

        ComboBox<String> startSelect = new ComboBox<>();
        startSelect.setPromptText("Start");
        startSelect.setStyle("-fx-font-size: 11px;");

        ComboBox<String> endSelect = new ComboBox<>();
        endSelect.setPromptText("End");
        endSelect.setStyle("-fx-font-size: 11px;");
        endSelect.setDisable(true);

        LocalDateTime temp = slot.getStart();
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        while (temp.isBefore(slot.getEnd())) {
            startSelect.getItems().add(temp.format(timeFmt));
            temp = temp.plusMinutes(30);
        }

        startSelect.setOnAction(e -> {
            endSelect.getItems().clear();
            endSelect.setDisable(false);

            if (startSelect.getValue() != null) {

                String[] parts = startSelect.getValue().split(":");
                int h = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);

                LocalDateTime selectedStart = slot.getStart().withHour(h).withMinute(m);
                LocalDateTime endTemp = selectedStart.plusMinutes(30);

                while (endTemp.isBefore(slot.getEnd()) || endTemp.isEqual(slot.getEnd())) {
                    endSelect.getItems().add(endTemp.format(timeFmt));
                    endTemp = endTemp.plusMinutes(30);
                }
            }
        });

        endSelect.setOnAction(e -> {
            if (startSelect.getValue() != null && endSelect.getValue() != null) {
                updateFinalSelection(slot.getStart(), startSelect.getValue(), endSelect.getValue());
            }
        });

        selectors.getChildren().addAll(new Label("Start:"), startSelect, new Label("End:"), endSelect);
        container.getChildren().addAll(header, selectors);

        rb.selectedProperty().addListener((obs, oldVal, newVal) -> {
            selectors.setVisible(newVal);
            if (!newVal) {
                startSelect.getSelectionModel().clearSelection();
                endSelect.getItems().clear();
                endSelect.setDisable(true);
            }
        });

        return container;
    }

    private void updateFinalSelection(LocalDateTime slotDate, String startStr, String endStr) {

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String dateStr = slotDate.format(dateFmt);
        this.finalSelectedRange = dateStr + " - " + startStr + " - " + endStr;
        System.out.println("Selected Range: " + this.finalSelectedRange);
    }

    public void setOnCancel(Runnable action) {
        this.onCancel = action;
    }

    public void setOnSave(java.util.function.Consumer<String> action) {
        this.onSave = action;
    }
}