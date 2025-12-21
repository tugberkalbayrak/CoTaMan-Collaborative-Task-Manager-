package com.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class NavBar extends HBox {

    private static final String ICON_HOME = "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z";
    private static final String ICON_ARCHIVE = "M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 12H4V8h16v10z";
    // --- EKLENDİ: Sync İkonu (Döngü okları) ---
    private static final String ICON_SYNC = "M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.03 20 13.57 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74C4.46 8.97 4 10.43 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z";

    private static final String ICON_SEARCH = "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z";
    private static final String ICON_SETTINGS = "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.56-1.62.94l-2.39-.96c.22.08.47 0 .59-.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z";
    private static final String ICON_FRIENDS = "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z";
    private static final String ICON_BELL = "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2zm-2 1H8v-6c0-2.48 1.51-4.5 4-4.5s4 2.02 4 4.5v6z";

    private Runnable onHomeClick;
    private Runnable onArchiveClick;
    private Runnable onSyncClick; // EKLENDİ

    public NavBar() {
        this.setPadding(new Insets(10, 20, 10, 20));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 0 0 15 15;");

        // --- Butonları Oluştur ---
        Button homeBtn = createIconButton(ICON_HOME);
        homeBtn.setOnAction(e -> {
            if (onHomeClick != null)
                onHomeClick.run();
        });

        Button archiveBtn = createIconButton(ICON_ARCHIVE);
        archiveBtn.setOnAction(e -> {
            if (onArchiveClick != null)
                onArchiveClick.run();
        });

        // --- EKLENDİ: Sync Butonu (Yeşil) ---
        Button syncBtn = createIconButton(ICON_SYNC);
        syncBtn.setStyle(
                "-fx-background-color: #27AE60; -fx-background-radius: 50%; -fx-min-width: 40px; -fx-min-height: 40px; -fx-padding: 0;");
        syncBtn.setOnAction(e -> {
            if (onSyncClick != null)
                onSyncClick.run();
        });

        Button searchBtn = createIconButton(ICON_SEARCH);
        Button settingsBtn = createIconButton(ICON_SETTINGS);
        Button friendsBtn = createIconButton(ICON_FRIENDS);
        Button notifBtn = createIconButton(ICON_BELL);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label logoLabel = new Label("COTAMAN");
        logoLabel.setFont(Theme.getHeaderFont());
        logoLabel.setStyle("-fx-text-fill: " + Theme.SECONDARY_COLOR + "; -fx-font-size: 24px; -fx-font-weight: bold;");

        this.getChildren().addAll(
                homeBtn, archiveBtn, syncBtn, // syncBtn buraya eklendi
                searchBtn,
                spacer,
                settingsBtn, friendsBtn, notifBtn, logoLabel);
    }

    public void setOnHomeClick(Runnable action) {
        this.onHomeClick = action;
    }

    public void setOnArchiveClick(Runnable action) {
        this.onArchiveClick = action;
    }

    public void setOnSyncClick(Runnable action) {
        this.onSyncClick = action;
    } // EKLENDİ

    private Button createIconButton(String svgPathData) {
        SVGPath path = new SVGPath();
        path.setContent(svgPathData);
        path.setFill(javafx.scene.paint.Color.WHITE);
        path.setScaleX(1.2);
        path.setScaleY(1.2);

        Button btn = new Button();
        btn.setGraphic(path);
        btn.setStyle("-fx-background-color: " + Theme.SECONDARY_COLOR
                + "; -fx-background-radius: 50%; -fx-min-width: 40px; -fx-min-height: 40px; -fx-padding: 0;");
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }
}