package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import com.example.ui.components.*;

public class ArchiveView extends StackPane {

    private NavBar navBar;
    private StackPane overlayContainer;

    public ArchiveView() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        navBar = new NavBar();
        mainLayout.setTop(navBar);

        mainLayout.setLeft(createSidebar());

        mainLayout.setCenter(createContentArea());

        overlayContainer = new StackPane();
        overlayContainer.setVisible(false);
        overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

        overlayContainer.setOnMouseClicked(e -> {
            if (e.getTarget() == overlayContainer) {
                overlayContainer.setVisible(false);
                overlayContainer.getChildren().clear();
            }
        });

        this.getChildren().addAll(mainLayout, overlayContainer);
    }

    public NavBar getNavBar() {
        return navBar;
    }

    private VBox createContentArea() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button searchTriggerBtn = createIconButton("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchTriggerBtn.setOnAction(e -> showSearchPanel());

        Button filterIconBtn = createIconButton("M10 18h4v-2h-4v2zM3 6v2h18V6H3zm3 7h12v-2H6v2z");


        Label searchFolderBtn = new Label("Search This Folder 🔍");
        searchFolderBtn.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-cursor: hand;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        CoTaButton uploadBtn = new CoTaButton("+ Add File", CoTaButton.StyleType.PRIMARY);
        uploadBtn.setOnAction(e -> showUploadPopup());

        topBar.getChildren().addAll(searchTriggerBtn, filterIconBtn, spacer, searchFolderBtn, uploadBtn);

        Label pathLabel = new Label("Public Archive > CS > CS102 > Syllabus");
        pathLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-font-style: italic;");

        VBox fileList = new VBox(10);
        fileList.getChildren().add(new FileItem("CS102_Syllabus_2025.pdf", "12 Oct 2025", "Uğur Güdükbay"));
        fileList.getChildren().add(new FileItem("Midterm_Review_Notes.docx", "15 Nov 2025", "Tuğberk Albayrak"));
        fileList.getChildren().add(new FileItem("Lab04_Solutions.zip", "20 Nov 2025", "Abdullah Özen"));
        fileList.getChildren().add(new FileItem("Lecture_Slides_Week7.pptx", "22 Nov 2025", "Sinan Çavdar"));
        fileList.getChildren().add(new FileItem("Final_Exam_2024.pdf", "05 Jan 2025", "Anonim"));

        ScrollPane scroll = new ScrollPane(fileList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        box.getChildren().addAll(topBar, pathLabel, scroll);
        return box;
    }

    private void showSearchPanel() {
        SearchFilterPopup popup = new SearchFilterPopup();
        
        popup.setOnSave(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });
        
        popup.setOnClear(() -> {
            System.out.println("Filtreler temizlendi");
        });

        StackPane.setAlignment(popup, Pos.TOP_RIGHT);
        StackPane.setMargin(popup, new Insets(60, 100, 0, 0));

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void showUploadPopup() {
        UploadFilePopup popup = new UploadFilePopup();
        
        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnSave(() -> {
            System.out.println("Dosya Yükleniyor: " + popup.getFileName());

            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        StackPane.setAlignment(popup, Pos.CENTER);
        StackPane.setMargin(popup, new Insets(50));

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private VBox createSidebar() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setPrefWidth(250);
        box.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1 + ";");

        Label title = new Label("Folders");
        title.setFont(Theme.getHeaderFont());
        title.setStyle("-fx-text-fill: " + Theme.SECONDARY_COLOR + "; -fx-font-size: 20px;");

        TreeItem<String> rootItem = new TreeItem<>("Public Archive");
        rootItem.setExpanded(true);

        TreeItem<String> csFolder = new TreeItem<>("CS");
        TreeItem<String> cs102 = new TreeItem<>("CS102");
        cs102.getChildren().addAll(
            new TreeItem<>("Syllabus"),
            new TreeItem<>("Old Exams"),
            new TreeItem<>("Lecture Notes")
        );
        csFolder.getChildren().add(cs102);

        TreeItem<String> mathFolder = new TreeItem<>("MATH");
        mathFolder.getChildren().add(new TreeItem<>("MATH102"));
        
        TreeItem<String> physFolder = new TreeItem<>("PHYS");
        physFolder.getChildren().addAll(new TreeItem<>("PHYS101"), new TreeItem<>("PHYS102"));

        rootItem.getChildren().addAll(csFolder, mathFolder, physFolder);

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-control-inner-background: " + Theme.PANEL_COLOR1 + ";" +
            "-fx-text-fill: white;"
        );
        
        box.getChildren().addAll(title, treeView);
        return box;
    }

    private Button createIconButton(String svgPath) {
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        path.setFill(javafx.scene.paint.Color.WHITE);
        Button btn = new Button();
        btn.setGraphic(path);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return btn;
    }
}