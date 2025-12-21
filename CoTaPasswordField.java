package com.example.ui.components;

import javafx.scene.control.PasswordField;

public class CoTaPasswordField extends PasswordField {

    public CoTaPasswordField(String promptText) {
        super();
        this.setPromptText(promptText);

        this.setStyle(
            "-fx-background-color: " + Theme.PANEL_COLOR2 + ";" + 
            "-fx-text-fill: white;" + 
            "-fx-prompt-text-fill: gray;" +
            "-fx-background-radius: 15;" + 
            "-fx-padding: 10;"
        );

        this.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                this.setStyle(
                    "-fx-background-color: " + Theme.PANEL_COLOR2 + ";" + 
                    "-fx-text-fill: white;" + 
                    "-fx-prompt-text-fill: gray;" +
                    "-fx-background-radius: 15;" + 
                    "-fx-padding: 10;" +
                    "-fx-border-color: " + Theme.SECONDARY_COLOR + ";" + 
                    "-fx-border-radius: 15; " + 
                    "-fx-border-width: 2;"
                );
            } else {
                this.setStyle(
                    "-fx-background-color: " + Theme.PANEL_COLOR2 + ";" + 
                    "-fx-text-fill: white;" + 
                    "-fx-prompt-text-fill: gray;" +
                    "-fx-background-radius: 15;" + 
                    "-fx-padding: 10;"
                );
            }
        });
    }
}
