package com.example.ui.components;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import com.example.Entity.CalendarEvent;
import com.example.Entity.Importance;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class CalendarGrid extends GridPane {

    private static final String[] TIME_SLOTS = {
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00",
            "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"
    };

    private static final String[] DAYS = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private static final int ROW_HEIGHT = 60;
    private static final int HEADER_HEIGHT = 30; // Başlık satırı yüksekliği
    private static final int START_HOUR = 8;

    private LocalDate currentWeekStart;
    private List<CalendarEvent> allEventsCache;
    private Label weekLabel;

    public CalendarGrid() {
        this.currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.allEventsCache = new ArrayList<>();
        setupGrid();
    }

    private void setupGrid() {
        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(10, 10, 50, 10)); // Alttan boşluk

        // --- SÜTUNLAR ---
        ColumnConstraints dayCol = new ColumnConstraints();
        dayCol.setMinWidth(100);
        dayCol.setHalignment(HPos.RIGHT);
        this.getColumnConstraints().add(dayCol);

        for (int i = 0; i < TIME_SLOTS.length; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(90);
            col.setHgrow(Priority.ALWAYS);
            this.getColumnConstraints().add(col);

            Label timeLabel = new Label(TIME_SLOTS[i]);
            timeLabel.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 10px; -fx-font-family: 'Segoe UI';");
            GridPane.setHalignment(timeLabel, HPos.CENTER);
            this.add(timeLabel, i + 1, 0); // Satır 0'a ekleniyor
        }

        // --- SATIRLAR (DÜZELTİLDİ) ---

        // 1. BAŞLIK SATIRI İÇİN KURAL (Row 0)
        RowConstraints headerRow = new RowConstraints();
        headerRow.setMinHeight(HEADER_HEIGHT);
        headerRow.setPrefHeight(HEADER_HEIGHT);
        headerRow.setVgrow(Priority.NEVER);
        this.getRowConstraints().add(headerRow); // İLK BUNU EKLİYORUZ

        // 2. GÜNLER İÇİN KURALLAR (Row 1-7)
        for (int row = 0; row < DAYS.length; row++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(ROW_HEIGHT);
            rowConst.setPrefHeight(ROW_HEIGHT);
            rowConst.setVgrow(Priority.NEVER); // Asla ezilme!

            this.getRowConstraints().add(rowConst); // Sonra bunları ekliyoruz

            Label dayLabel = new Label(DAYS[row]);
            dayLabel.setStyle(
                    "-fx-text-fill: " + Theme.SECONDARY_COLOR + "; -fx-font-weight: bold; -fx-font-size: 12px;");
            this.add(dayLabel, 0, row + 1);

            for (int col = 0; col < TIME_SLOTS.length; col++) {
                Pane emptySlot = new Pane();
                emptySlot.setStyle("-fx-background-color: #383838; -fx-background-radius: 5; -fx-opacity: 0.8;");
                this.add(emptySlot, col + 1, row + 1);
            }
        }
    }

    public void loadEvents(List<CalendarEvent> events) {
        this.allEventsCache = events;
        renderCurrentWeek();
    }

    public void changeWeek(int weeksToAdd) {
        this.currentWeekStart = this.currentWeekStart.plusWeeks(weeksToAdd);
        renderCurrentWeek();
    }

    public void resetToToday() {
        this.currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        renderCurrentWeek();
    }

    public void setWeekLabel(Label label) {
        this.weekLabel = label;
        updateLabelText();
    }

    private void updateLabelText() {
        if (weekLabel != null) {
            LocalDate weekEnd = currentWeekStart.plusDays(6);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");
            weekLabel.setText(currentWeekStart.format(fmt) + " - " + weekEnd.format(fmt));
        }
    }

    private void renderCurrentWeek() {
        // Sadece VBox (Event) olanları temizle, Label ve Pane'ler kalsın
        this.getChildren().removeIf(node -> node instanceof VBox);
        updateLabelText();

        LocalDate weekEnd = currentWeekStart.plusDays(6);

        for (CalendarEvent event : allEventsCache) {
            try {
                LocalDateTime start = event.getStartTime();
                LocalDateTime end = event.getEndTime();
                LocalDate eventDate = start.toLocalDate();

                boolean isInWeek = (eventDate.isEqual(currentWeekStart) || eventDate.isAfter(currentWeekStart)) &&
                        (eventDate.isEqual(weekEnd) || eventDate.isBefore(weekEnd));

                if (isInWeek) {
                    int dayIndex = start.getDayOfWeek().getValue() - 1;
                    int hourIndex = start.getHour() - START_HOUR;

                    if (hourIndex >= 0 && hourIndex < TIME_SLOTS.length) {
                        long durationHours = Duration.between(start, end).toHours();
                        long durationMinutes = Duration.between(start, end).toMinutes() % 60;
                        int colSpan = (int) durationHours;
                        if (durationMinutes > 0)
                            colSpan++;
                        if (colSpan < 1)
                            colSpan = 1;

                        if (hourIndex + colSpan > TIME_SLOTS.length) {
                            colSpan = TIME_SLOTS.length - hourIndex;
                        }

                        placeEventOnGrid(event, dayIndex, hourIndex, colSpan);
                    }
                }
            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
            }
        }
    }

    private void placeEventOnGrid(CalendarEvent event, int dayIndex, int timeIndex, int colSpan) {
        String title = event.getTitle() != null ? event.getTitle() : "";
        String color = "#8E44AD";

        if (title.contains("Midterm") || title.contains("Final") || title.contains("Exam")) {
            color = "#C0392B";
        } else if (title.contains("Lecture") || title.contains("Class") || title.contains("Lab")) {
            color = "#2980B9";
        } else if (event.getImportance() == Importance.MUST) {
            color = "#E67E22";
        }

        VBox eventBox = new VBox();
        eventBox.setAlignment(Pos.CENTER);
        eventBox.setPadding(new Insets(2));
        eventBox.setMaxWidth(Double.MAX_VALUE);
        eventBox.setFillWidth(true);

        eventBox.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 3, 0, 0, 1);");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 9px;");
        titleLbl.setWrapText(true);
        titleLbl.setAlignment(Pos.CENTER);

        String timeStr = event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                " - " +
                event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        Label timeLbl = new Label(timeStr);
        timeLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 8px;");

        eventBox.getChildren().addAll(titleLbl, timeLbl);
        Tooltip.install(eventBox, new Tooltip(title + "\n" + event.getLocation() + "\n" + timeStr));

        this.add(eventBox, timeIndex + 1, dayIndex + 1, colSpan, 1);
    }

    public void addEvent(int dayIndex, int timeIndex, String title, String colorCode) {
        VBox eventBox = new VBox();
        eventBox.setAlignment(Pos.CENTER);
        eventBox.setStyle("-fx-background-color: " + colorCode + "; -fx-background-radius: 8;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 9px;");
        eventBox.getChildren().add(titleLbl);
        this.add(eventBox, timeIndex + 1, dayIndex + 1);
    }
}