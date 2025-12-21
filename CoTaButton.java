package com.example.ui.components;

import javafx.scene.control.Button;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class CoTaButton extends Button {

    public enum StyleType {
        PRIMARY,
        SECONDARY,
        DANGER
    }

    public CoTaButton(String text, StyleType type) {
        super(text);
        setupStyle(type);

        this.setCursor(Cursor.HAND);

        this.setEffect(new DropShadow(5, Color.BLACK));
    }

    private void setupStyle(StyleType type) {
        String baseStyle = "-fx-background-radius: 20; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-size: 14px; " +
                           "-fx-padding: 10 20 10 20;";

        String colorStyle = "";

        switch (type) {
            case PRIMARY:
                colorStyle = "-fx-background-color: " + Theme.PRIMARY_COLOR + ";";
                break;
            case SECONDARY:
                colorStyle = "-fx-background-color: " + Theme.SECONDARY_COLOR + ";";
                break;
            case DANGER:
                colorStyle = "-fx-background-color: #C0392B;";
                break;
        }

        this.setStyle(baseStyle + colorStyle);

        String finalColorStyle = colorStyle;
        this.setOnMouseEntered(e -> this.setStyle(baseStyle + finalColorStyle.replace(";", "") + "; -fx-opacity: 0.8;"));
        this.setOnMouseExited(e -> this.setStyle(baseStyle + finalColorStyle));
    }
}
