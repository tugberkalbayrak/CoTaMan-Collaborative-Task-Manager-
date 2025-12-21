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
import com.example.Entity.Importance;

import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class MainView extends StackPane {

    private java.util.function.Consumer<String> onGroupClick;
    private StackPane overlayContainer;

    // DEĞİŞİKLİK 1: mainLayout sınıf seviyesine çıktı
    private BorderPane mainLayout;

    private CalendarGrid calendarGrid;
    private NavBar navBar;
    private VBox groupsBox;

    public MainView() {
        // 1. Ana Layout (BorderPane)
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        navBar = new NavBar();
        mainLayout.setTop(navBar);

        // SRS Bağlantısı
        navBar.setOnSyncClick(() -> showSRSPopup());

        // DEĞİŞİKLİK 2: Home butonuna basınca Takvime dön
        navBar.setOnHomeClick(() -> mainLayout.setCenter(createCenterArea()));

        VBox groupsPanel = createGroupsPanel();
        mainLayout.setRight(groupsPanel);

        // --- ORTA ALAN: TAKVİM (Metot ile çağırıyoruz) ---
        mainLayout.setCenter(createCenterArea());

        // FAB Butonu
        Button addEventFab = new Button("+");
        addEventFab.setStyle("-fx-background-color: " + Theme.PRIMARY_COLOR
                + "; -fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 60px; -fx-min-height: 60px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 5);");
        addEventFab.setCursor(javafx.scene.Cursor.HAND);
        addEventFab.setOnAction(e -> showAddEventPopup());
        StackPane.setAlignment(addEventFab, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(addEventFab, new Insets(0, 30, 30, 0));

        overlayContainer = new StackPane();
        overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlayContainer.setVisible(false);

        this.getChildren().addAll(mainLayout, addEventFab, overlayContainer);
    }

    // DEĞİŞİKLİK 3: Orta alanı (Takvimi) oluşturan metot eklendi
    private VBox createCenterArea() {
        VBox centerArea = new VBox(10);
        centerArea.setPadding(new Insets(10));

        // 1. Hafta Navigasyonu
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

        weekNav.getChildren().addAll(prevWeekBtn, todayBtn, weekDateLabel, nextWeekBtn);

        // 2. Takvim Izgarası
        calendarGrid = new CalendarGrid();
        calendarGrid.setWeekLabel(weekDateLabel);

        // Verileri Yükle
        calendarGrid.loadEvents(SessionManager.getInstance().getUserEvents());

        // Buton Olayları
        prevWeekBtn.setOnAction(e -> calendarGrid.changeWeek(-1));
        nextWeekBtn.setOnAction(e -> calendarGrid.changeWeek(1));
        todayBtn.setOnAction(e -> calendarGrid.resetToToday());

        ScrollPane scrollPane = new ScrollPane(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false); // Takvim uzun kalsın, scroll olsun
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
                System.out.println("Grup Oluşturuluyor: " + name);
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
                // DEĞİŞİKLİK 4: Group nesnesini gönderiyoruz
                groupsBox.getChildren().add(createGroupItem(group));
    }

    // DEĞİŞİKLİK 5: createGroupItem artık Group nesnesi alıyor ve GroupView'a
    // yönlendiriyor
    private Label createGroupItem(Group group) {
        Label lbl = new Label("● " + group.getGroupName());
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5;");
        lbl.setCursor(javafx.scene.Cursor.HAND);

        lbl.setOnMouseClicked(e -> {
            // GroupView sayfasını oluştur (Artık sadece içerik, navbar yok)
            GroupView groupContent = new GroupView(group);

            // Ekranın ortasına yerleştir
            mainLayout.setCenter(groupContent);

            // NOT: Geri dönüş butonu nerede?
            // MainView'daki ana NavBar'da zaten var!
            // navBar.setOnHomeClick(...) zaten MainView constructor'ında tanımlı.
            // Home'a basınca otomatik olarak Takvim'e dönecek.
        });

        return lbl;
    }

    public void setOnGroupClick(java.util.function.Consumer<String> action) {
        this.onGroupClick = action;
    }

    // ... (Popup metotları aynı) ...

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
                    () -> System.out.println("SMS Yanlış!"));
        });
        overlayContainer.getChildren().clear();
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
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
                // UI'a geçici ekle
                calendarGrid.addEvent(dayIndex, timeIndex, name, color);
                // DB'ye kaydet
                LocalDate targetDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .plusDays(dayIndex);
                String startStr = targetDate.toString() + "T" + (String.format("%02d", 8 + timeIndex)) + ":00";
                String endStr = targetDate.toString() + "T" + (String.format("%02d", 9 + timeIndex)) + ":00";

                SessionManager.getInstance().addEvent(name, startStr, endStr, com.example.Entity.Importance.MUST);

                calendarGrid.loadEvents(SessionManager.getInstance().getUserEvents());
            }
            overlayContainer.getChildren().clear();
            overlayContainer.setVisible(false);
        });
        overlayContainer.getChildren().add(popup);
        overlayContainer.setVisible(true);
    }
}