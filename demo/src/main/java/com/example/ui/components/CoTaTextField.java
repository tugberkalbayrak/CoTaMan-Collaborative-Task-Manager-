package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;

public class CoTaTextField extends TextField {

    public CoTaTextField(String promptText) {
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
                this.setStyle(this.getStyle() + "-fx-border-color: " + Theme.SECONDARY_COLOR + "; -fx-border-radius: 15; -fx-border-width: 2;");
            } else {
                this.setStyle(this.getStyle().split("-fx-border-color")[0]);
            }
        });
    }
}