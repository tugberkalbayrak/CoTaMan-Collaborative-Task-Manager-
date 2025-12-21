package com.example.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane; // Kullanmasak da import kalsın hata vermesin
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane; // Popup için gerekli
import javafx.scene.layout.VBox;
import com.example.ui.components.*;
import com.example.Entity.Group;
import com.example.Entity.User;
import com.example.Manager.SessionManager;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;

// BorderPane yerine StackPane yapıyoruz ki Popup açabilelim
public class GroupView extends StackPane {

    private Group currentGroup;
    private TableView<Task> taskTable;
    private StackPane overlayContainer;

    public GroupView(Group group) {
        this.currentGroup = group;
        this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        // --- NAV BAR SİLİNDİ (Çünkü MainView'da zaten var) ---

        // ANA İÇERİK KUTUSU
        HBox contentBox = new HBox(20);
        contentBox.setPadding(new Insets(20));

        // SOL KOLON (Task Tracker + Meetings) - Esnek genişlik
        VBox leftColumn = createLeftColumn();
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // SAĞ KOLON (Files + Members) - Sabit genişlik
        VBox rightColumn = createRightColumn();
        rightColumn.setPrefWidth(300);
        rightColumn.setMinWidth(300);

        contentBox.getChildren().addAll(leftColumn, rightColumn);

        // Popup Overlay (Gizli)
        overlayContainer = new StackPane();
        overlayContainer.setVisible(false);
        overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Hepsini StackPane'e ekle (İçerik altta, Overlay üstte)
        this.getChildren().addAll(contentBox, overlayContainer);
    }

    // --- NavBar Getter ARTIK YOK ---

    // --- SOL KOLON (Görevler ve Toplantı) ---
    private VBox createLeftColumn() {
        VBox box = new VBox(20);

        // Başlık Alanı
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

        // Task Ekleme Butonu
        CoTaButton addTaskBtn = new CoTaButton("+ Add Task", CoTaButton.StyleType.PRIMARY);
        addTaskBtn.setOnAction(e -> showAddTaskPopup());

        header.getChildren().addAll(titleBox, spacer, addTaskBtn);

        // Tablo (Task Tracker)
        taskTable = new TableView<>();
        styleTable(taskTable);

        // Kolonlar
        TableColumn<Task, String> colTask = new TableColumn<>("Task Name");
        colTask.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Task, String> colOwner = new TableColumn<>("Assigned To");
        colOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));

        TableColumn<Task, String> colDate = new TableColumn<>("Due Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        TableColumn<Task, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Status Rengini Ayarlama
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

        // Verileri Yükle
        refreshTasks();

        box.getChildren().addAll(header, taskTable);
        return box;
    }

    // --- SAĞ KOLON (Dosyalar ve Üyeler) ---
    private VBox createRightColumn() {
        VBox box = new VBox(20);

        // 1. Grup Dosyaları
        VBox fileSection = new VBox(10);
        fileSection.setStyle(
                "-fx-background-color: " + Theme.PANEL_COLOR1 + "; -fx-background-radius: 15; -fx-padding: 15;");

        Label filesTitle = new Label("Recent Files");
        filesTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        VBox fileList = new VBox(5);
        fileList.getChildren().add(new Label("• Syllabus.pdf")); // Dummy data
        Label uploadLink = new Label("↑ Upload File");
        uploadLink.setStyle("-fx-text-fill: " + Theme.PRIMARY_COLOR
                + "; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 12px;");

        fileSection.getChildren().addAll(filesTitle, fileList, uploadLink);

        // 2. Üyeler
        VBox memberSection = new VBox(10);
        memberSection.setStyle(
                "-fx-background-color: " + Theme.PANEL_COLOR1 + "; -fx-background-radius: 15; -fx-padding: 15;");

        Label memberTitle = new Label("Members");
        memberTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        HBox avatars = new HBox(10);
        // Backend'den üyeleri çek
        for (User member : SessionManager.getInstance().getGroupMembers(currentGroup)) {
            String initials = member.getFullName().substring(0, 1).toUpperCase();
            avatars.getChildren().add(createAvatar(initials));
        }

        memberSection.getChildren().addAll(memberTitle, avatars);

        box.getChildren().addAll(fileSection, memberSection);
        return box;
    }

    // POPUP MANTIĞI
    private void showAddTaskPopup() {
        AddTaskPopup popup = new AddTaskPopup();

        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnAdd((name, date) -> {
            // Backend'e kaydet
            SessionManager.getInstance().addTask(currentGroup, name, date, "In Progress");
            refreshTasks();

            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
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

    private void styleTable(TableView<?> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400); // Tablo yüksekliği
        table.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1 + "; -fx-control-inner-background: "
                + Theme.PANEL_COLOR1 + "; -fx-table-cell-border-color: transparent; -fx-text-fill: white;");
        VBox.setVgrow(table, Priority.ALWAYS);
    }
}