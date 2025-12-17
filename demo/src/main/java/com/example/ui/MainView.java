package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.example.ui.components.*;


public class MainView extends StackPane {

    private StackPane overlayContainer;
    private CalendarGrid calendarGrid;
    private NavBar navBar;

    public MainView() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        navBar = new NavBar(); 
        mainLayout.setTop(navBar);

        VBox groupsPanel = createGroupsPanel();
        mainLayout.setRight(groupsPanel);

        calendarGrid = new CalendarGrid();

        calendarGrid.addEvent(0, 2, "CS102", "#8E44AD");
        calendarGrid.addEvent(0, 3, "CS102", "#8E44AD");
        calendarGrid.addEvent(1, 0, "TURK102", "#E67E22");
        calendarGrid.addEvent(2, 4, "MATH102 Exam", "#C0392B"); 

        ScrollPane scrollPane = new ScrollPane(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        mainLayout.setCenter(scrollPane);

        Button addEventFab = new Button("+");
        addEventFab.setStyle(
            "-fx-background-color: " + Theme.PRIMARY_COLOR + ";" + 
            "-fx-text-fill: white;" +
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 50%;" + 
            "-fx-min-width: 60px; -fx-min-height: 60px;" +
            "-fx-max-width: 60px; -fx-max-height: 60px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 5);"
        );
        addEventFab.setCursor(javafx.scene.Cursor.HAND);

        addEventFab.setOnAction(e -> showAddEventPopup());

        StackPane.setAlignment(addEventFab, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(addEventFab, new Insets(0, 30, 30, 0));

        overlayContainer = new StackPane();
        overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlayContainer.setVisible(false);

        this.getChildren().addAll(mainLayout, addEventFab, overlayContainer);
    }

    public NavBar getNavBar() {
        return navBar;
    }

    public void showAddEventPopup() {
        AddEventPopup popup = new AddEventPopup();

        popup.setOnCancel(() -> {
            overlayContainer.getChildren().clear();
            overlayContainer.setVisible(false);
        });

        popup.setOnSave(() -> {
            String name = popup.getEventName();
            int dayIndex = popup.getDayIndex();
            int timeIndex = popup.getTimeIndex();
            String color = popup.getSelectedColor();

            if (name != null && !name.isEmpty() && dayIndex >= 0 && timeIndex >= 0) {
                calendarGrid.addEvent(dayIndex, timeIndex, name, color);
                System.out.println("Yeni etkinlik eklendi: " + name);
            }

            overlayContainer.getChildren().clear();
            overlayContainer.setVisible(false);
        });

        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private VBox createGroupsPanel() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20, 20, 20, 10)); 
        box.setAlignment(Pos.TOP_CENTER);

        box.setStyle("-fx-background-color: " + Theme.SECONDARY_COLOR + "; -fx-background-radius: 30 0 0 30;");
        box.setPrefWidth(200);

        Label title = new Label("Groups");
        title.setStyle(
            "-fx-text-fill: " + Theme.SECONDARY_COLOR + ";" + 
            "-fx-font-size: 20px; " + 
            "-fx-font-weight: bold; " + 
            "-fx-background-color: " + Theme.TEXT_WHITE + ";" + 
            "-fx-background-radius: 10; " + 
            "-fx-padding: 5 20 5 20;"
        );
        
        box.getChildren().add(title);

        box.getChildren().add(createGroupItem("Eng 101", "#8E44AD")); 
        box.getChildren().add(createGroupItem("Turk 102", "#E67E22")); 
        box.getChildren().add(createGroupItem("Math 102", "#3498DB")); 

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Label addGroupBtn = new Label("+");
        addGroupBtn.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        addGroupBtn.setCursor(javafx.scene.Cursor.HAND);

        box.getChildren().addAll(spacer, addGroupBtn);
        return box;
    }

    private Label createGroupItem(String name, String colorCode) {
        Label lbl = new Label("● " + name);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5;");
        lbl.setCursor(javafx.scene.Cursor.HAND);
        return lbl;
    }
}