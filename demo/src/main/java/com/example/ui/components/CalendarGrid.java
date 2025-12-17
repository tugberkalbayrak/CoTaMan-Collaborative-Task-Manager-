package com.example.ui.components;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class CalendarGrid extends GridPane {

    private static final String[] TIME_SLOTS = {
        "8:30-9:20", "9:30-10:20", "10:30-11:20", "11:30-12:20", 
        "12:30-13:20", "13:30-14:20", "14:30-15:20", "15:30-16:20", "16:30-17:20"
    };

    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private static final int ROW_HEIGHT = 60; 

    public CalendarGrid() {
        this.setHgap(10); 
        this.setVgap(15); 
        this.setPadding(new Insets(20));
        
        ColumnConstraints dayCol = new ColumnConstraints();
        dayCol.setMinWidth(120);
        dayCol.setHalignment(HPos.RIGHT);
        this.getColumnConstraints().add(dayCol);

        for (int i = 0; i < TIME_SLOTS.length; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(120); 
            col.setHgrow(Priority.ALWAYS);
            this.getColumnConstraints().add(col);

            Label timeLabel = new Label(TIME_SLOTS[i]);
            timeLabel.setStyle(
                "-fx-text-fill: " + Theme.SECONDARY_COLOR + ";" + 
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-color: #E0E0E0;" + 
                "-fx-background-radius: 10;" +
                "-fx-padding: 2 8 2 8;"
            );
            GridPane.setHalignment(timeLabel, HPos.CENTER);
            this.add(timeLabel, i + 1, 0); 
        }
        
        for (int row = 0; row < DAYS.length; row++) {

            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(ROW_HEIGHT);
            rowConst.setPrefHeight(ROW_HEIGHT);
            this.getRowConstraints().add(rowConst);

            Label dayLabel = new Label(DAYS[row]);
            dayLabel.setStyle(
                "-fx-text-fill: " + Theme.SECONDARY_COLOR + ";" +
                "-fx-border-color: " + Theme.SECONDARY_COLOR + ";" +
                "-fx-border-radius: 15;" +
                "-fx-font-weight: bold;" + 
                "-fx-alignment: center;"
            );
            dayLabel.setMinWidth(110);
            dayLabel.setMinHeight(ROW_HEIGHT);

            this.add(dayLabel, 0, row + 1);

            for (int col = 0; col < TIME_SLOTS.length; col++) {
                Pane emptySlot = new Pane();
                emptySlot.setStyle(
                    "-fx-background-color: #383838;" + 
                    "-fx-background-radius: 10;"
                );
                this.add(emptySlot, col + 1, row + 1);
            }
        }
    }

    public void addEvent(int dayIndex, int timeIndex, String title, String colorCode) {
        VBox eventBox = new VBox();
        eventBox.setAlignment(Pos.CENTER);
        eventBox.setStyle(
            "-fx-background-color: " + colorCode + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);"
        );

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        titleLbl.setWrapText(true);

        eventBox.getChildren().add(titleLbl);

        this.add(eventBox, timeIndex + 1, dayIndex + 1);
    }
}