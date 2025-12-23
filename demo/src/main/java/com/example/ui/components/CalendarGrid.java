package com.example.ui.components;

import com.example.Entity.CalendarEvent;
import com.example.Entity.Importance;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarGrid extends BorderPane {

    // --- AYARLAR ---
    private static final int START_HOUR = 6; // Başlangıç: 06:00
    private static final int END_HOUR = 24; // Bitiş: 24:00
    private static final int HOUR_WIDTH = 100; // Her saat 100 piksel genişliğinde
    private static final double MINUTE_WIDTH = HOUR_WIDTH / 60.0;
    private static final int ROW_HEIGHT = 80; // Her günün satır yüksekliği

    private LocalDate currentWeekStart;
    private List<CalendarEvent> allEvents;
    private Label weekLabel;

    // Satır Referansları
    private List<Pane> dayRows;

    public CalendarGrid() {
        this.allEvents = new ArrayList<>();
        this.currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");
        refresh();
    }

    private void refresh() {
        HBox mainContainer = new HBox(0);
        mainContainer.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        // 1. SOL KOLON: Gün Başlıkları
        VBox daysHeader = createDaysHeader();

        // 2. SAĞ KOLON: Zaman Çizelgesi
        ScrollPane timeScroll = createTimeLineArea();
        HBox.setHgrow(timeScroll, Priority.ALWAYS);

        mainContainer.getChildren().addAll(daysHeader, timeScroll);

        this.setCenter(mainContainer);
        updateWeekLabel();
    }

    // --- SOL TARAFTAKİ GÜN İSİMLERİ (Özellik Eklendi) ---
    private VBox createDaysHeader() {
        VBox headerBox = new VBox(0);
        headerBox.setMinWidth(100);
        headerBox.setPrefWidth(100);
        headerBox.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1
                + "; -fx-border-color: #34495E; -fx-border-width: 0 1 0 0;");

        // Üst Boşluk (Header hizası)
        Region spacer = new Region();
        spacer.setPrefHeight(30);
        headerBox.getChildren().add(spacer);

        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");

        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            boolean isToday = date.equals(LocalDate.now());

            VBox dayCell = new VBox(5);
            dayCell.setPrefHeight(ROW_HEIGHT);
            dayCell.setAlignment(Pos.CENTER);
            // Varsayılan Stil
            String defaultStyle = "-fx-border-color: #34495E; -fx-border-width: 0 0 1 0; -fx-cursor: hand;";
            dayCell.setStyle(defaultStyle);

            Label nameLbl = new Label(days[i]);
            nameLbl.setStyle("-fx-text-fill: #BDC3C7; -fx-font-weight: bold; -fx-font-size: 14px;");

            Label dateLbl = new Label(date.format(fmt));
            dateLbl.setStyle("-fx-text-fill: " + (isToday ? Theme.PRIMARY_COLOR : "gray") + "; -fx-font-size: 12px;");

            dayCell.getChildren().addAll(nameLbl, dateLbl);

            // --- YENİ EKLENEN ÖZELLİK: GÜN ÖZETİ PANELİ ---
            setupDayHoverPopup(dayCell, date);

            // Hover Efekti (Arka plan rengi değişsin)
            dayCell.setOnMouseEntered(e -> {
                dayCell.setStyle(
                        "-fx-border-color: #34495E; -fx-border-width: 0 0 1 0; -fx-background-color: #34495E; -fx-cursor: hand;");
                // Popup'ı açma işlemi setupDayHoverPopup içinde zaten var, burası sadece
                // görsellik
            });
            dayCell.setOnMouseExited(e -> {
                dayCell.setStyle(defaultStyle);
            });

            headerBox.getChildren().add(dayCell);
        }
        return headerBox;
    }

    // --- YENİ: GÜN ÖZETİ POPUP MANTIĞI ---
    private void setupDayHoverPopup(VBox dayNode, LocalDate date) {
        Popup popup = new Popup();

        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10));
        // Koyu Mavi/Gri Arka Plan, Yeşil Çerçeve
        panel.setStyle("-fx-background-color: #2C3E50; -fx-border-color: " + Theme.PRIMARY_COLOR
                + "; -fx-border-width: 1; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy (EEEE)");
        Label title = new Label(date.format(fmt));
        title.setStyle("-fx-text-fill: " + Theme.PRIMARY_COLOR
                + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 5 0; -fx-border-color: white; -fx-border-width: 0 0 1 0;");

        VBox eventsList = new VBox(5);
        boolean hasEvents = false;

        if (allEvents != null) {
            // O güne ait etkinlikleri bul ve saate göre sırala
            List<CalendarEvent> daysEvents = allEvents.stream()
                    .filter(e -> e.getStartTime().toLocalDate().equals(date))
                    .sorted(Comparator.comparing(CalendarEvent::getStartTime))
                    .collect(Collectors.toList());

            for (CalendarEvent event : daysEvents) {
                hasEvents = true;
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);

                // Saat
                String timeStr = event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                Label timeLbl = new Label(timeStr);
                timeLbl.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 11px; -fx-font-family: 'Monospaced';");

                // Renkli Nokta
                Region dot = new Region();
                dot.setPrefSize(8, 8);
                String color = getEventColor(event.getImportance());
                dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50%;");

                // Başlık
                Label nameLbl = new Label(event.getTitle());
                nameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
                nameLbl.setMaxWidth(150); // Çok uzun isimleri kes

                row.getChildren().addAll(timeLbl, dot, nameLbl);
                eventsList.getChildren().add(row);
            }
        }

        if (!hasEvents) {
            Label empty = new Label("No events for this day.");
            empty.setStyle("-fx-text-fill: #95A5A6; -fx-font-style: italic; -fx-font-size: 11px;");
            eventsList.getChildren().add(empty);
        }

        panel.getChildren().addAll(title, eventsList);
        popup.getContent().add(panel);

        // Olayları Bağla
        // NOT: createDaysHeader içindeki setOnMouseEntered ile çakışmaması için
        // oradaki efekti buraya da ekleyebiliriz veya Popup'ı ayrı bir listener ile
        // açabiliriz.
        // En temiz yöntem: Event Filter kullanmak veya sırayla eklemek.

        dayNode.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, e -> {
            popup.show(dayNode, e.getScreenX() + 15, e.getScreenY());
        });

        dayNode.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_EXITED, e -> {
            popup.hide();
        });
    }

    // --- SAĞ TARAFTAKİ ZAMAN ALANI (AYNI KALDI) ---
    private ScrollPane createTimeLineArea() {
        VBox contentBox = new VBox(0);
        contentBox.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");

        // ÜST: Saat Etiketleri
        HBox timeHeader = new HBox(0);
        timeHeader.setPrefHeight(30);
        timeHeader.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1
                + "; -fx-border-color: #34495E; -fx-border-width: 0 0 1 0;");

        for (int i = START_HOUR; i < END_HOUR; i++) {
            Label timeLbl = new Label(String.format("%02d:00", i));
            timeLbl.setMinWidth(HOUR_WIDTH);
            timeLbl.setPrefWidth(HOUR_WIDTH);
            timeLbl.setAlignment(Pos.CENTER_LEFT);
            timeLbl.setPadding(new Insets(0, 0, 0, 5));
            timeLbl.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 11px;");

            Region tick = new Region();
            tick.setMinWidth(1);
            tick.setStyle("-fx-background-color: #34495E;");

            HBox cell = new HBox(timeLbl);
            cell.setStyle("-fx-border-color: #34495E; -fx-border-width: 0 1 0 0; -fx-border-style: dashed;");
            timeHeader.getChildren().add(cell);
        }
        contentBox.getChildren().add(timeHeader);

        // ALT: 7 Günlük Satırlar
        dayRows = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            AnchorPane row = new AnchorPane();
            row.setPrefHeight(ROW_HEIGHT);
            row.setPrefWidth((END_HOUR - START_HOUR) * HOUR_WIDTH);
            row.setStyle("-fx-border-color: #34495E; -fx-border-width: 0 0 1 0; -fx-border-style: solid;");

            for (int h = 0; h < (END_HOUR - START_HOUR); h++) {
                Region line = new Region();
                line.setPrefWidth(1);
                line.setPrefHeight(ROW_HEIGHT);
                line.setStyle("-fx-background-color: #2C3E50; -fx-opacity: 0.3;");
                line.setLayoutX(h * HOUR_WIDTH);
                row.getChildren().add(line);
            }

            dayRows.add(row);
            contentBox.getChildren().add(row);
        }

        placeEvents();

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(false);
        scroll.setStyle("-fx-background: " + Theme.BG_COLOR + "; -fx-background-color: " + Theme.BG_COLOR + ";");
        return scroll;
    }

    private void placeEvents() {
        if (allEvents == null)
            return;

        for (CalendarEvent event : allEvents) {
            LocalDateTime start = event.getStartTime();
            LocalDateTime end = event.getEndTime();

            if (start.toLocalDate().isBefore(currentWeekStart)
                    || start.toLocalDate().isAfter(currentWeekStart.plusDays(6))) {
                continue;
            }

            int dayIndex = start.getDayOfWeek().getValue() - 1;
            Pane targetRow = dayRows.get(dayIndex);

            if (start.getHour() < START_HOUR || start.getHour() >= END_HOUR)
                continue;

            long minutesFromStart = Duration.between(
                    LocalDateTime.of(start.toLocalDate(), LocalTime.of(START_HOUR, 0)),
                    start).toMinutes();

            long durationMinutes = Duration.between(start, end).toMinutes();

            double layoutX = minutesFromStart * MINUTE_WIDTH;
            double width = durationMinutes * MINUTE_WIDTH;
            if (width < 10)
                width = 10;

            StackPane eventBox = new StackPane();
            String color = getEventColor(event.getImportance());
            eventBox.setStyle("-fx-background-color: " + color
                    + "; -fx-background-radius: 5; -fx-opacity: 0.9; -fx-border-color: white; -fx-border-width: 0 0 0 3;");

            Label titleLbl = new Label(event.getTitle());
            titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
            titleLbl.setMaxWidth(width - 5);
            eventBox.getChildren().add(titleLbl);
            StackPane.setAlignment(titleLbl, Pos.CENTER_LEFT);
            eventBox.setPadding(new Insets(0, 0, 0, 5));

            targetRow.getChildren().add(eventBox);
            eventBox.setLayoutX(layoutX);
            eventBox.setPrefWidth(width);
            eventBox.setPrefHeight(ROW_HEIGHT - 10);
            eventBox.setLayoutY(5);

            setupHoverPopup(eventBox, event);
        }
    }

    private void setupHoverPopup(StackPane eventNode, CalendarEvent event) {
        Popup popup = new Popup();

        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #2C3E50; -fx-border-color: " + Theme.PRIMARY_COLOR
                + "; -fx-border-width: 1; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label title = new Label(event.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label time = new Label("🕒 " + event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - "
                + event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        time.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px;");

        Label desc = new Label(event.getDescription());
        desc.setWrapText(true);
        desc.setMaxWidth(250);
        desc.setStyle("-fx-text-fill: white;");

        panel.getChildren().addAll(title, time, desc);
        popup.getContent().add(panel);

        eventNode.setOnMouseEntered(e -> {
            popup.show(eventNode, e.getScreenX() + 10, e.getScreenY() + 10);
            eventNode.setStyle(eventNode.getStyle() + "-fx-effect: dropshadow(three-pass-box, white, 10, 0, 0, 0);");
        });

        eventNode.setOnMouseExited(e -> {
            popup.hide();
            String color = getEventColor(event.getImportance());
            eventNode.setStyle("-fx-background-color: " + color
                    + "; -fx-background-radius: 5; -fx-opacity: 0.9; -fx-border-color: white; -fx-border-width: 0 0 0 3;");
        });
    }

    public void setWeekLabel(Label lbl) {
        this.weekLabel = lbl;
        updateWeekLabel();
    }

    public void loadEvents(List<CalendarEvent> events) {
        this.allEvents = events;
        refresh();
    }

    public void changeWeek(int weeksToAdd) {
        this.currentWeekStart = this.currentWeekStart.plusWeeks(weeksToAdd);
        refresh();
    }

    public void resetToToday() {
        this.currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        refresh();
    }

    // MainView'daki fab butonu için boş metot (Hata vermemesi için)
    public void addEvent(int dayIndex, int timeIndex, String name, String color) {
    }

    private void updateWeekLabel() {
        if (weekLabel != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");
            String start = currentWeekStart.format(fmt);
            String end = currentWeekStart.plusDays(6).format(fmt);
            weekLabel.setText(start + " - " + end);
        }
    }

    private String getEventColor(Importance imp) {
        if (imp == null)
            return "#95A5A6";
        switch (imp) {
            case MUST:
                return "#C0392B";
            case OPTIONAL:
                return "#E67E22";
            case TRIVIA:
                return "#27AE60";
            default:
                return "#3498DB";
        }
    }
}