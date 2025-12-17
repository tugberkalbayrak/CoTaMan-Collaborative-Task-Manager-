package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class FileItem extends HBox {

    private static final String ICON_FILE = "M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z";

    private static final String ICON_DOWNLOAD = "M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z";

    public FileItem(String fileName, String uploadDate, String uploaderName) {

        this.setPadding(new Insets(10));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle(
            "-fx-background-color: " + Theme.PANEL_COLOR1 + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #555;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Button fileIcon = createIconButton(ICON_FILE, Theme.SECONDARY_COLOR);

        Label nameLbl = new Label(fileName);
        nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label detailsLbl = new Label("Uploaded by " + uploaderName + " • " + uploadDate);
        detailsLbl.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-font-size: 11px;");

        javafx.scene.layout.VBox infoBox = new javafx.scene.layout.VBox(5, nameLbl, detailsLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button downloadBtn = createIconButton(ICON_DOWNLOAD, "#3498DB");

        this.getChildren().addAll(fileIcon, infoBox, spacer, downloadBtn);

        this.setOnMouseEntered(e -> this.setStyle(
            "-fx-background-color: #4A4A4A;" + 
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        this.setOnMouseExited(e -> this.setStyle(
            "-fx-background-color: " + Theme.PANEL_COLOR1 + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #555;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 0 0 1 0;"
        ));
    }

    private Button createIconButton(String svgData, String color) {
        SVGPath path = new SVGPath();
        path.setContent(svgData);
        path.setFill(javafx.scene.paint.Color.web(color));
        
        Button btn = new Button();
        btn.setGraphic(path);
        btn.setStyle("-fx-background-color: transparent;");
        return btn;
    }
}