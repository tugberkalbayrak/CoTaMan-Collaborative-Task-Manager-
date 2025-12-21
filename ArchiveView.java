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
import com.example.Manager.SessionManager; // Backend Bağlantısı
import com.example.Entity.AcademicFile; // Veri Modeli
import java.util.List;

public class ArchiveView extends StackPane {

    private NavBar navBar;
    private StackPane overlayContainer;

    // Listeyi sınıf seviyesine çıkardık ki her yerden erişip yenileyebilelim
    private VBox fileListVBox;

    public ArchiveView() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        navBar = new NavBar();
        mainLayout.setTop(navBar);

        mainLayout.setLeft(createSidebar());

        mainLayout.setCenter(createContentArea());

        // Overlay (Pop-up perdesi)
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

        // --- ÜST BAR ---
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

        // --- BREADCRUMB ---
        Label pathLabel = new Label("Public Archive > All Files");
        pathLabel.setStyle("-fx-text-fill: " + Theme.TEXT_GRAY + "; -fx-font-style: italic;");

        // --- DOSYA LİSTESİ ---
        fileListVBox = new VBox(10); // VBox'ı başlattık

        // Sayfa açılır açılmaz veritabanından verileri çek
        refreshFileList();

        ScrollPane scroll = new ScrollPane(fileListVBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        box.getChildren().addAll(topBar, pathLabel, scroll);
        return box;
    }

    // --- VERİTABANINDAN LİSTEYİ YENİLEME METODU ---
    private void refreshFileList() {
        // 1. Mevcut listeyi temizle
        fileListVBox.getChildren().clear();

        // 2. SessionManager üzerinden veritabanındaki dosyaları çek
        List<AcademicFile> dbFiles = SessionManager.getInstance().getPublicFiles();

        if (dbFiles.isEmpty()) {
            Label emptyLbl = new Label("No files found. Be the first to upload!");
            emptyLbl.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
            fileListVBox.getChildren().add(emptyLbl);
        } else {
            // 3. Her dosya için bir FileItem oluştur
            for (AcademicFile file : dbFiles) {
                String uploaderName = (file.getUploader() != null) ? file.getUploader().getFullName() : "Unknown";

                // FileItem(FileName, Date, Uploader)
                FileItem item = new FileItem(file.getFileName(), "Recently", uploaderName);
                fileListVBox.getChildren().add(item);
            }
        }
        System.out.println("Arşiv yenilendi. Dosya sayısı: " + dbFiles.size());
    }

    // --- UPLOAD POPUP VE KAYIT ---
    private void showUploadPopup() {
        UploadFilePopup popup = new UploadFilePopup();

        popup.setOnCancel(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnSave(() -> {
            // Verileri Formdan Al
            String name = popup.getFileName();
            String course = popup.getCourse();
            String type = popup.getFileType();
            String visibility = popup.getVisibility();

            if (name != null && !name.isEmpty()) {
                // 1. Veritabanına Kaydet (SessionManager Aracılığıyla)
                SessionManager.getInstance().uploadFile(name, course, type, visibility);

                // 2. Listeyi Yenile (Anında görünsün)
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

    // --- SEARCH PANEL (Görsel Kalıyor) ---
    private void showSearchPanel() {
        SearchFilterPopup popup = new SearchFilterPopup();

        popup.setOnSave(() -> {
            overlayContainer.setVisible(false);
            overlayContainer.getChildren().clear();
        });

        popup.setOnClear(() -> {
            System.out.println("Filtreler temizlendi");
            refreshFileList(); // Temizleyince tüm listeyi geri getir
        });

        StackPane.setAlignment(popup, Pos.TOP_RIGHT);
        StackPane.setMargin(popup, new Insets(60, 100, 0, 0));

        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }

    // --- SIDEBAR (Aynı kalıyor) ---
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
                new TreeItem<>("Lecture Notes"));
        csFolder.getChildren().add(cs102);

        TreeItem<String> mathFolder = new TreeItem<>("MATH");
        mathFolder.getChildren().add(new TreeItem<>("MATH102"));

        rootItem.getChildren().addAll(csFolder, mathFolder);

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: " + Theme.PANEL_COLOR1 + ";" +
                        "-fx-text-fill: white;");

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