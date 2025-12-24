package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.example.ui.components.*;
import com.example.Manager.SessionManager;
import com.example.Entity.Group;

import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class MainView extends StackPane {

    private StackPane overlayContainer;

    private BorderPane mainLayout;

    private CalendarGrid calendarGrid;
    private NavBar navBar;
    private VBox groupsBox;

    public MainView() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        navBar = new NavBar();
        mainLayout.setTop(navBar);

        navBar.setOnSyncClick(() -> showSRSPopup());

        navBar.setOnFriendsClick(this::showAddFriendPopup);

        navBar.setOnHomeClick(() -> mainLayout.setCenter(createCenterArea()));

        navBar.setOnSettingsClick(this::showSettings);
        navBar.setOnNotificationsClick(this::showNotifications);

        VBox groupsPanel = createGroupsPanel();
        mainLayout.setRight(groupsPanel);

        mainLayout.setCenter(createCenterArea());

        overlayContainer = new StackPane();
        overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlayContainer.setVisible(false);

        this.getChildren().addAll(mainLayout, overlayContainer);
    }

    private VBox createCenterArea() {
        VBox centerArea = new VBox(10);
        centerArea.setPadding(new Insets(10));

        HBox weekNav = new HBox(15);
        weekNav.setAlignment(Pos.CENTER);
        weekNav.setPadding(new Insets(0, 0, 10, 0));

        Button prevWeekBtn = new Button("◄ Previous Week");
        styleNavBtn(prevWeekBtn);

        Button todayBtn = new Button("Today");
        styleNavBtn(todayBtn);

        Button nextWeekBtn = new Button("Next Week ►");
        styleNavBtn(nextWeekBtn);

        Label weekDateLabel = new Label("Loading...");
        weekDateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button addEventBtn = new Button("Add Event +");
        addEventBtn.setStyle("-fx-background-color: " + Theme.PRIMARY_COLOR
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 8 15 8 15;");
        addEventBtn.setOnAction(e -> showAddEventPopup());

        weekNav.getChildren().addAll(prevWeekBtn, todayBtn, nextWeekBtn, spacer1, weekDateLabel, spacer2, addEventBtn);

        calendarGrid = new CalendarGrid();
        calendarGrid.setWeekLabel(weekDateLabel);

        calendarGrid.loadEvents(SessionManager.getInstance().getUserEvents());

        calendarGrid.setOnDeleteRequest(event -> {
            SessionManager.getInstance().deleteEvent(event);
            calendarGrid.loadEvents(SessionManager.getInstance().getUserEvents());
        });

        prevWeekBtn.setOnAction(e -> calendarGrid.changeWeek(-1));
        nextWeekBtn.setOnAction(e -> calendarGrid.changeWeek(1));
        todayBtn.setOnAction(e -> calendarGrid.resetToToday());

        ScrollPane scrollPane = new ScrollPane(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        centerArea.getChildren().addAll(weekNav, scrollPane);
        return centerArea;
    }

    private void styleNavBtn(Button btn) {
        btn.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
    }

    public NavBar getNavBar() {
        return navBar;
    }

    public CalendarGrid getCalendarGrid() {
        return calendarGrid;
    }

    private VBox createGroupsPanel() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20, 20, 20, 10));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: " + Theme.SECONDARY_COLOR + "; -fx-background-radius: 30 0 0 30;");
        box.setPrefWidth(200);
        Label title = new Label("Groups");
        title.setStyle("-fx-text-fill: " + Theme.SECONDARY_COLOR
                + "; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: " + Theme.TEXT_WHITE
                + "; -fx-background-radius: 10; -fx-padding: 5 20 5 20;");
        box.getChildren().add(title);
        groupsBox = new VBox(10);
        groupsBox.setAlignment(Pos.TOP_CENTER);

        refreshGroupList();

        box.getChildren().add(groupsBox);
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label addGroupBtn = new Label("+");
        addGroupBtn.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        addGroupBtn.setCursor(javafx.scene.Cursor.HAND);

        addGroupBtn.setOnMouseClicked(e -> {
            CreateGroupPopup popup = new CreateGroupPopup();

            popup.setOnCancel(() -> {
                overlayContainer.setVisible(false);
                overlayContainer.getChildren().clear();
            });

            popup.setOnCreate((name, course) -> {
                System.out.println("Creating Group: " + name);
                SessionManager.getInstance().createGroup(name, course);
                refreshGroupList();
                overlayContainer.setVisible(false);
                overlayContainer.getChildren().clear();
            });

            overlayContainer.getChildren().clear();
            overlayContainer.getChildren().add(popup);
            overlayContainer.setVisible(true);
        });

        box.getChildren().addAll(spacer, addGroupBtn);
        return box;
    }

    public void refreshGroupList() {
        groupsBox.getChildren().clear();
        List<Group> myGroups = SessionManager.getInstance().getUserGroups();
        if (myGroups.isEmpty())
            groupsBox.getChildren().add(new Label("No groups"));
        else
            for (Group group : myGroups)
                groupsBox.getChildren().add(createGroupItem(group));
    }

    public void showSRSPopup() {
        SRSPopup popup = new SRSPopup();
        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });
        popup.setOnLoginRequest((id, pass) -> {
            SessionManager.getInstance().startSrsLogin(id, pass, (result) -> {
                if (result == 2)
                    popup.switchToSmsMode();
                else if (result == 1)
                    overlayContainer.setVisible(false);
            });
        });
        popup.setOnSmsVerify((code) -> {
            SessionManager.getInstance().verifySmsAndFetch(code,
                    () -> {
                        overlayContainer.setVisible(false);
                        calendarGrid.loadEvents(SessionManager.getInstance().getUserEvents());
                    },
                    () -> System.out.println("SMS Incorrect!"));
        });
        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private Label createGroupItem(Group group) {
        Label lbl = new Label("● " + group.getGroupName());
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5;");
        lbl.setCursor(javafx.scene.Cursor.HAND);

        lbl.setOnMouseClicked(e -> {
            try {
                com.example.ui.components.NotificationManager.showInfo("Group Access",
                        "Opening group: " + group.getGroupName());

                GroupView groupContent = new GroupView(group);
                mainLayout.setCenter(groupContent);
            } catch (Exception ex) {
                ex.printStackTrace();
                com.example.ui.components.NotificationManager.showError("Navigation Error",
                        "Failed to open group: " + ex.getMessage());
            }
        });

        return lbl;
    }

    public void showAddEventPopup() {
        AddEventPopup popup = new AddEventPopup();

        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnSave(() -> {
            String name = popup.getEventName();
            int dayIndex = popup.getDayIndex();
            String startTime = popup.getStartTime();
            String endTime = popup.getEndTime();

            com.example.Entity.Importance selectedImp = popup.getSelectedImportance();

            if (name != null && !name.isEmpty() && dayIndex >= 0 && startTime != null && endTime != null
                    && selectedImp != null) {

                java.time.LocalDate targetDate = java.time.LocalDate.now()
                        .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                        .plusDays(dayIndex);

                String startIso = targetDate.toString() + "T" + startTime + ":00";
                String endIso = targetDate.toString() + "T" + endTime + ":00";

                String result = SessionManager.getInstance().addEvent(name, startIso, endIso, selectedImp);

                if (result.equals("Success")) {
                    calendarGrid.loadEvents(SessionManager.getInstance().getUserEvents());
                    overlayContainer.getChildren().clear();
                    overlayContainer.setVisible(false);
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Scheduling Conflict");
                    alert.setHeaderText("Cannot Add Event");
                    alert.setContentText(result);
                    alert.showAndWait();
                }
            }
        });

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void showAddFriendPopup() {
        AddFriendPopup popup = new AddFriendPopup();
        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnAdd((email) -> {
            String result = SessionManager.getInstance().addFriend(email);
            popup.setStatus(result, result.contains("Success"));
        });

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void showSettings() {
        mainLayout.setCenter(new SettingsView());
    }

    private void showNotifications() {
        mainLayout.setCenter(new NotificationsView());
    }
}