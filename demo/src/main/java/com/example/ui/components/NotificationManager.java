package com.example.ui.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

    private static final double PAD = 20;
    private static final double WIDTH = 350;
    private static final double HEIGHT = 80;
    private static final double SPACING = 10;

    private static final List<Popup> activePopups = new ArrayList<>();

    public enum Type {
        INFO, SUCCESS, ERROR
    }

    public static void showInfo(String title, String message) {
        show(title, message, Type.INFO);
    }

    public static void showSuccess(String title, String message) {
        show(title, message, Type.SUCCESS);
    }

    public static void showError(String title, String message) {
        show(title, message, Type.ERROR);
    }

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    private static void show(String title, String message, Type type) {
        Platform.runLater(() -> {
            Popup popup = createPopup(title, message, type);
            // Use the set primary stage as owner if available
            popup.show(primaryStage);

            activePopups.add(popup);
            repositionPopups();

            // Auto-close
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
                popup.hide();
                activePopups.remove(popup);
                repositionPopups();
            }));
            timeline.play();
        });
    }

    private static Popup createPopup(String title, String message, Type type) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(false);

        VBox root = new VBox(5);
        root.setPadding(new Insets(15));
        root.setPrefWidth(WIDTH);
        root.setAlignment(Pos.CENTER_LEFT);

        String bgColor = Theme.PANEL_COLOR1;
        String accentColor = "#3498DB";

        switch (type) {
            case SUCCESS:
                accentColor = Theme.SECONDARY_COLOR;
                break;
            case ERROR:
                accentColor = "#C0392B";
                break;
            case INFO:
                accentColor = Theme.PRIMARY_COLOR;
                break;
        }

        root.setStyle("-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + accentColor + ";" +
                "-fx-border-width: 0 0 0 5;" +
                "-fx-border-radius: 10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        Label titleLbl = new Label(title);
        titleLbl.setFont(Theme.getRegularFont());
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label msgLbl = new Label(message);
        msgLbl.setFont(Theme.getRegularFont());
        msgLbl.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-font-size: 14px;");
        msgLbl.setWrapText(true);

        root.getChildren().addAll(titleLbl, msgLbl);
        popup.getContent().add(root);

        return popup;
    }

    private static void repositionPopups() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double startX = screenBounds.getMaxX() - WIDTH - PAD;
        double startY = screenBounds.getMaxY() - PAD;

        for (int i = 0; i < activePopups.size(); i++) {
            Popup p = activePopups.get(activePopups.size() - 1 - i);
            double yPos = startY - (activePopups.size() - 1 - i) * (rootHeight(p) + SPACING) - rootHeight(p);

            p.setX(startX);
            p.setY(yPos);
        }
    }

    private static double rootHeight(Popup p) {
        if (p.getContent().isEmpty())
            return HEIGHT;
        return p.getContent().get(0).getBoundsInLocal().getHeight() > 10
                ? p.getContent().get(0).getBoundsInLocal().getHeight()
                : HEIGHT;
    }
}
