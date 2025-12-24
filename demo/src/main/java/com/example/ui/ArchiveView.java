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
import com.example.Manager.SessionManager;
import com.example.Entity.AcademicFile;
import java.util.List;

public class ArchiveView extends StackPane {

    private NavBar navBar;
    private StackPane overlayContainer;

    private VBox fileListVBox;

    private boolean isPrivateView = false;
    private Label pathLabel;

    private String selectedCourseCode = null;

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

        Button searchTriggerBtn = createIconButton(
                "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchTriggerBtn.setOnAction(e -> showSearchPanel());

        Button filterIconBtn = createIconButton("M10 18h4v-2h-4v2zM3 6v2h18V6H3zm3 7h12v-2H6v2z");

        Label searchFolderBtn = new Label("Search This Folder 🔍");
        searchFolderBtn.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        CoTaButton uploadBtn = new CoTaButton("+ Add File", CoTaButton.StyleType.PRIMARY);
        uploadBtn.setOnAction(e -> showUploadPopup());

        topBar.getChildren().addAll(searchTriggerBtn, filterIconBtn, spacer, searchFolderBtn, uploadBtn);

        pathLabel = new Label("Public Archive > All Files");
        pathLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-font-style: italic;");

        fileListVBox = new VBox(10);

        refreshFileList();

        ScrollPane scroll = new ScrollPane(fileListVBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        box.getChildren().addAll(topBar, pathLabel, scroll);
        return box;
    }

    private void refreshFileList() {

        fileListVBox.getChildren().clear();

        List<AcademicFile> dbFiles;
        if (isPrivateView) {
            dbFiles = SessionManager.getInstance().getPrivateFiles();
        } else if (selectedCourseCode != null) {
            dbFiles = SessionManager.getInstance().getFilesByCourse(selectedCourseCode);
        } else {
            dbFiles = SessionManager.getInstance().getPublicFiles();
        }
        if (dbFiles.isEmpty()) {
            Label emptyLbl = new Label(isPrivateView ? "You have no private files." : "No public files found.");
            emptyLbl.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
            fileListVBox.getChildren().add(emptyLbl);
        } else {
            for (AcademicFile file : dbFiles) {
                String uploaderName = (file.getUploader() != null) ? file.getUploader().getFullName() : "Unknown";

                FileItem item = new FileItem(file.getFileName(), "Recently", uploaderName);

                item.setOnAction(() -> {
                    SessionManager.getInstance().getRepository().openFile(file);
                });

                fileListVBox.getChildren().add(item);
            }
        }
    }

    private void showUploadPopup() {
        UploadFilePopup popup = new UploadFilePopup();

        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnSave(() -> {

            String name = popup.getFileName();
            String course = popup.getCourse();
            String type = popup.getFileType();
            String visibility = popup.getVisibility();

            java.io.File selectedFile = popup.getSelectedFile();
            String filePath = (selectedFile != null) ? selectedFile.getAbsolutePath() : "";
            if (name != null && !name.isEmpty()) {

                SessionManager.getInstance().uploadFile(name, filePath, course, type, visibility);

                refreshFileList();
            }
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        StackPane.setAlignment(popup, Pos.CENTER);
        StackPane.setMargin(popup, new Insets(50));

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    private void showSearchPanel() {
        SearchFilterPopup popup = new SearchFilterPopup();
        popup.setOnSave(() -> {

            String query = popup.getSearchQuery();
            String sortBy = popup.getSortBy();
            String type = popup.getFilterType();
            String vis = popup.getFilterVisibility();

            List<AcademicFile> rawList;
            if (isPrivateView) {
                rawList = SessionManager.getInstance().getPrivateFiles();
            } else if (selectedCourseCode != null) {
                rawList = SessionManager.getInstance().getFilesByCourse(selectedCourseCode);
            } else {
                rawList = SessionManager.getInstance().getPublicFiles();
            }

            List<AcademicFile> filteredList = SessionManager.getInstance()
                    .searchAndFilterFiles(rawList, query, sortBy, type, vis);

            fileListVBox.getChildren().clear();
            if (filteredList.isEmpty()) {
                Label empty = new Label("No results found for '" + query + "'");
                empty.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
                fileListVBox.getChildren().add(empty);
            } else {
                for (AcademicFile file : filteredList) {
                    String uploaderName = (file.getUploader() != null) ? file.getUploader().getFullName() : "Unknown";
                    FileItem item = new FileItem(file.getFileName(), "Recently", uploaderName);
                    item.setOnAction(() -> SessionManager.getInstance().getRepository().openFile(file));
                    fileListVBox.getChildren().add(item);
                }
            }
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });
        popup.setOnClear(() -> {

            refreshFileList();
        });
        StackPane.setAlignment(popup, Pos.TOP_RIGHT);
        StackPane.setMargin(popup, new Insets(60, 100, 0, 0));
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

        TreeItem<String> root = new TreeItem<>("Root");

        TreeItem<String> privateItem = new TreeItem<>("Private Archive (My Files)");

        TreeItem<String> publicItem = new TreeItem<>("Public Archive");
        publicItem.setExpanded(true);

        TreeItem<String> csFolder = new TreeItem<>("CS");
        csFolder.getChildren().addAll(new TreeItem<>("CS102"), new TreeItem<>("CS201"));
        publicItem.getChildren().add(csFolder);

        TreeItem<String> mathFolder = new TreeItem<>("MATH");
        mathFolder.getChildren().add(new TreeItem<>("MATH102"));
        publicItem.getChildren().add(mathFolder);
        root.getChildren().addAll(privateItem, publicItem);
        TreeView<String> treeView = new TreeView<>(root);
        treeView.setShowRoot(false);
        treeView.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: " + Theme.PANEL_COLOR1 + ";" +
                        "-fx-text-fill: white;");

        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String val = newVal.getValue();

                if (val.matches("[A-Z]{2,4}[0-9]{3}")) {
                    selectedCourseCode = val;
                    isPrivateView = false;
                    if (pathLabel != null)
                        pathLabel.setText("Public Archive > " + val);
                }

                else if (val.contains("Private")) {
                    selectedCourseCode = null;
                    isPrivateView = true;
                    if (pathLabel != null)
                        pathLabel.setText("Private Archive > My Files");
                }

                else {
                    selectedCourseCode = null;
                    isPrivateView = false;
                    if (pathLabel != null)
                        pathLabel.setText("Public Archive > All Files");
                }
                refreshFileList();
            }
        });
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