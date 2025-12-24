package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.example.ui.components.*;
import com.example.Entity.Group;
import com.example.Entity.User;
import com.example.Manager.SessionManager;
import javafx.collections.FXCollections;
import java.util.List;

public class GroupView extends StackPane {

    private Group currentGroup;
    private TableView<Task> taskTable;
    private StackPane overlayContainer;

    private HBox avatarsContainer;
    private VBox fileListContainer;

    public GroupView(Group group) {
        this.currentGroup = group;
        this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        HBox contentBox = new HBox(20);
        contentBox.setPadding(new Insets(20));

        VBox leftColumn = createLeftColumn();
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox rightColumn = createRightColumn();
        rightColumn.setPrefWidth(300);
        rightColumn.setMinWidth(300);

        contentBox.getChildren().addAll(leftColumn, rightColumn);

        overlayContainer = new StackPane();
        overlayContainer.setVisible(false);
        overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        this.getChildren().addAll(contentBox, overlayContainer);
    }

    private VBox createLeftColumn() {
        VBox box = new VBox(20);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label title = new Label(currentGroup.getGroupName());
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: " + Theme.TEXT_WHITE + "; -fx-font-size: 24px;");

        Label subTitle = new Label("Course: " + currentGroup.getCourseCode());
        subTitle.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + ";");
        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonsBox = new HBox(10);
        CoTaButton meetingBtn = new CoTaButton("⏱ Schedule", CoTaButton.StyleType.SECONDARY);
        meetingBtn.setOnAction(e -> showMeetingPopup());

        CoTaButton addTaskBtn = new CoTaButton("+ Task", CoTaButton.StyleType.PRIMARY);
        addTaskBtn.setOnAction(e -> showAddTaskPopup());

        buttonsBox.getChildren().addAll(meetingBtn, addTaskBtn);
        header.getChildren().addAll(titleBox, spacer, buttonsBox);

        VBox meetingSection = createMeetingSection();

        taskTable = new TableView<>();
        styleTable(taskTable);

        taskTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem itemComplete = new MenuItem("Mark as Completed");
            itemComplete.setOnAction(e -> updateTaskStatus(row.getItem(), "Completed", "#27AE60"));

            MenuItem itemProgress = new MenuItem("Mark as In Progress");
            itemProgress.setOnAction(e -> updateTaskStatus(row.getItem(), "In Progress", "#E67E22"));

            MenuItem itemNotStarted = new MenuItem("Mark as Not Started");
            itemNotStarted.setOnAction(e -> updateTaskStatus(row.getItem(), "Not Started", "#C0392B"));

            javafx.scene.control.SeparatorMenuItem sep = new javafx.scene.control.SeparatorMenuItem();

            MenuItem itemDelete = new MenuItem("Delete Task");
            itemDelete.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            itemDelete.setOnAction(e -> {
                Task taskToDelete = row.getItem();
                SessionManager.getInstance().deleteTask(currentGroup, taskToDelete);
                refreshTasks();
            });

            contextMenu.getItems().addAll(itemProgress, itemComplete, itemNotStarted, sep, itemDelete);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));
            return row;
        });

        TableColumn<Task, String> colTask = new TableColumn<>("Task Name");
        colTask.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Task, String> colOwner = new TableColumn<>("Assigned To");
        colOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));

        TableColumn<Task, String> colDate = new TableColumn<>("Due Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        TableColumn<Task, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    try {
                        Task task = getTableView().getItems().get(getIndex());
                        setStyle("-fx-text-fill: " + task.getColor()
                                + "; -fx-font-weight: bold; -fx-alignment: center;");
                    } catch (Exception e) {
                    }
                }
            }
        });

        taskTable.getColumns().addAll(colTask, colOwner, colDate, colStatus);
        refreshTasks();

        box.getChildren().addAll(header, meetingSection, taskTable);
        return box;
    }

    private VBox createRightColumn() {
        VBox box = new VBox(20);

        VBox fileSection = new VBox(10);
        fileSection.setStyle(
                "-fx-background-color: " + Theme.PANEL_COLOR1 + "; -fx-background-radius: 15; -fx-padding: 15;");
        Label filesTitle = new Label("Recent Files");
        filesTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        fileListContainer = new VBox(5);
        refreshFiles();

        Label uploadLink = new Label("↑ Upload File");
        uploadLink.setStyle("-fx-text-fill: #27AE60; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 12px;");

        uploadLink.setOnMouseClicked(e -> showFileUploadPopup());

        fileSection.getChildren().addAll(filesTitle, fileListContainer, uploadLink);

        VBox memberSection = new VBox(10);
        memberSection.setStyle(
                "-fx-background-color: " + Theme.PANEL_COLOR1 + "; -fx-background-radius: 15; -fx-padding: 15;");

        HBox memberHeader = new HBox();
        memberHeader.setAlignment(Pos.CENTER_LEFT);
        Label memberTitle = new Label("Members");
        memberTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        Region spacerMember = new Region();
        HBox.setHgrow(spacerMember, Priority.ALWAYS);

        Label addMemberBtn = new Label("+");
        addMemberBtn.setStyle("-fx-text-fill: #27AE60; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand;");
        addMemberBtn.setOnMouseClicked(e -> showAddMemberPopup());

        memberHeader.getChildren().addAll(memberTitle, spacerMember, addMemberBtn);

        avatarsContainer = new HBox(10);
        for (User member : SessionManager.getInstance().getGroupMembers(currentGroup)) {
            String initials = getInitials(member.getFullName());
            avatarsContainer.getChildren().add(createAvatar(initials));
        }

        memberSection.getChildren().addAll(memberHeader, avatarsContainer);
        box.getChildren().addAll(fileSection, memberSection);
        return box;
    }

    private void showFileUploadPopup() {
        UploadFilePopup popup = new UploadFilePopup();
        popup.lockVisibility("Group Only");
        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnSave(() -> {
            if (popup.getSelectedFile() == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please select a file!");
                alert.show();
                return;
            }
            SessionManager.getInstance().uploadFileToGroup(
                    currentGroup,
                    popup.getFileName(),
                    popup.getSelectedFile().getAbsolutePath(),
                    popup.getFileType(),
                    popup.getVisibility());
            refreshFiles();
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void refreshFiles() {
        fileListContainer.getChildren().clear();
        if (currentGroup.getGroupArchive() != null) {
            for (com.example.Entity.AcademicFile file : currentGroup.getGroupArchive()) {
                Label fileLbl = new Label("• " + file.getFileName());
                fileLbl.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px; -fx-cursor: hand;");

                // Open file on click
                fileLbl.setOnMouseClicked(e -> {
                    SessionManager.getInstance().getRepository().openFile(file);
                });

                fileListContainer.getChildren().add(fileLbl);
            }
        }
    }

    private VBox createMeetingSection() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(0, 0, 15, 0));

        container.setPadding(new Insets(0, 0, 15, 0));

        Label sectionTitle = new Label("Group Meetings");
        sectionTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox listContainer = new VBox(8);
        List<com.example.Entity.CalendarEvent> allEvents = SessionManager.getInstance().getUserEvents();
        boolean found = false;
        String searchTitle = "Meeting: " + currentGroup.getGroupName();

        for (com.example.Entity.CalendarEvent event : allEvents) {
            if (event.getTitle().startsWith(searchTitle)) {
                found = true;
                listContainer.getChildren().add(createMeetingItem(event));
            }
        }

        if (!found) {
            Label empty = new Label("No meetings scheduled yet.");
            empty.setStyle("-fx-text-fill: #7F8C8D; -fx-font-style: italic;");
            listContainer.getChildren().add(empty);
        }

        container.getChildren().addAll(sectionTitle, listContainer);
        return container;
    }

    private HBox createMeetingItem(com.example.Entity.CalendarEvent event) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #34495E; -fx-background-radius: 10;");

        boolean isPast = event.getEndTime().isBefore(java.time.LocalDateTime.now());

        Label statusIcon = new Label(isPast ? "Past" : "Upcoming");
        statusIcon.setStyle(
                "-fx-text-fill: " + (isPast ? "#95A5A6" : "#F1C40F") + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        statusIcon.setMinWidth(70);

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd MMM - HH:mm");
        Label dateLbl = new Label(event.getStartTime().format(fmt));
        dateLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(dateLbl, spacer, statusIcon);
        if (isPast)
            row.setOpacity(0.6);
        return row;
    }

    private void showAddMemberPopup() {
        AddMemberPopup popup = new AddMemberPopup();
        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnAdd((selectedFriend) -> {
            String result = SessionManager.getInstance().addMemberToGroup(currentGroup, selectedFriend);
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Add Member Status");
            alert.setHeaderText(null);
            alert.setContentText(result);
            alert.showAndWait();

            if (result.contains("Success")) {
                String initials = getInitials(selectedFriend.getFullName());
                avatarsContainer.getChildren().add(createAvatar(initials));
            }
        });
        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void showAddTaskPopup() {
        AddTaskPopup popup = new AddTaskPopup();
        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });
        popup.setOnAdd((name, date) -> {
            SessionManager.getInstance().addTask(currentGroup, name, date, "In Progress");
            refreshTasks();
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });
        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void showMeetingPopup() {
        List<com.example.Entity.TimeSlot> suggestions = SessionManager.getInstance().findCommonSlots(currentGroup);
        MeetingSchedulerPopup popup = new MeetingSchedulerPopup(suggestions);
        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnSave((time) -> {
            SessionManager.getInstance().scheduleMeeting(currentGroup, time);
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();

            SessionManager.getInstance().scheduleMeeting(currentGroup, time);
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();

            if (getParent() instanceof BorderPane) {
                ((BorderPane) getParent()).setCenter(new GroupView(currentGroup));
            }
        });
        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void updateTaskStatus(Task task, String status, String color) {
        if (task == null)
            return;

        task.setStatus(status);
        task.setColor(color);

        SessionManager.getInstance().updateTaskStatus(currentGroup, task.getName(), status, color);

        taskTable.refresh();
    }

    private void refreshTasks() {
        if (currentGroup.getTasks() != null) {
            taskTable.setItems(FXCollections.observableArrayList(currentGroup.getTasks()));
        }
    }

    private Label createAvatar(String initials) {
        Label avatar = new Label(initials);
        avatar.setStyle("-fx-background-color: " + Theme.PRIMARY_COLOR
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 35px; -fx-min-height: 35px; -fx-alignment: center;");
        return avatar;
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty())
            return "?";
        String[] parts = fullName.split(" ");
        String initials = "";
        if (parts.length > 0)
            initials += parts[0].substring(0, 1);
        if (parts.length > 1)
            initials += parts[parts.length - 1].substring(0, 1);
        return initials.toUpperCase();
    }

    private void styleTable(TableView<?> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        table.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1 + "; -fx-control-inner-background: "
                + Theme.PANEL_COLOR1 + "; -fx-table-cell-border-color: transparent; -fx-text-fill: white;");
        VBox.setVgrow(table, Priority.ALWAYS);
    }
}