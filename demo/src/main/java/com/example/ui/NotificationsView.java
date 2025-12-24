package com.example.ui;

import com.example.Entity.CalendarEvent;
import com.example.Entity.Importance;
import com.example.Manager.SessionManager;
import com.example.ui.components.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsView extends VBox {

  public NotificationsView() {
    this.setPadding(new Insets(30));
    this.setSpacing(20);
    this.setStyle("-fx-background-color: " + Theme.BG_COLOR + ";");
    this.setAlignment(Pos.TOP_CENTER);

    Label header = new Label("Notifications");
    header.setFont(Theme.getHeaderFont());
    header.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

    VBox listContainer = new VBox(15);
    listContainer.setMaxWidth(600);
    listContainer.setAlignment(Pos.TOP_CENTER);

    List<CalendarEvent> events = SessionManager.getInstance().getUserEvents();

java.time.LocalDateTime now = java.time.LocalDateTime.now();
    List<CalendarEvent> importantEvents = events.stream()
        .filter(e -> e.getEndTime().isAfter(now))  
        .filter(this::isImportant)
        .sorted(Comparator.comparing(CalendarEvent::getStartTime))
        .collect(Collectors.toList());

    if (importantEvents.isEmpty()) {
      Label placeHolder = new Label("No upcoming important exams or events.");
      placeHolder.setStyle("-fx-text-fill: #95A5A6; -fx-font-size: 16px;");
      listContainer.getChildren().add(placeHolder);
    } else {
      for (CalendarEvent event : importantEvents) {
        listContainer.getChildren().add(createNotificationCard(event));
      }
    }

    ScrollPane scroll = new ScrollPane(listContainer);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
    scroll.setPannable(true);
    VBox.setVgrow(scroll, Priority.ALWAYS);

    this.getChildren().addAll(header, scroll);
  }

  private boolean isImportant(CalendarEvent e) {
    String t = e.getTitle().toLowerCase();
     
    if (t.contains("lecture") || t.contains("class") || t.contains("lab") || t.contains("recitation")) {
      return false;
    }
     
    return t.contains("exam") || t.contains("midterm") || t.contains("final")
        || t.contains("quiz") || t.contains("assignment") || t.contains("project")
        || e.getImportance() == Importance.CRITICAL || e.getImportance() == Importance.MUST;
  }

  private HBox createNotificationCard(CalendarEvent event) {
    HBox card = new HBox(15);
    card.setPadding(new Insets(15));
    card.setAlignment(Pos.CENTER_LEFT);
    card.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1
        + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);");

Region bar = new Region();
    bar.setMinWidth(5);
    bar.setPrefHeight(50);

String color = "#3498DB";  
    String t = event.getTitle() != null ? event.getTitle() : "";

    if (t.contains("Midterm") || t.contains("Final") || t.contains("Exam")
        || event.getImportance() == Importance.CRITICAL) {
      color = "#C0392B";  
    } else if (t.contains("Quiz") || t.contains("Assignment") || t.contains("Project")
        || event.getImportance() == Importance.MUST) {
      color = "#E67E22";  
    } else if (event.getImportance() == Importance.TRIVIA) {
      color = "#27AE60";  
    }

    bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3;");

    VBox content = new VBox(5);

    Label title = new Label(event.getTitle());
    title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

    String timeStr = event.getStartTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")) + " - " +
        event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    Label time = new Label(timeStr);
    time.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px;");

    content.getChildren().addAll(title, time);

    card.getChildren().addAll(bar, content);

card.setOnMouseEntered(e -> card.setStyle(
        "-fx-background-color: #3E3E3E; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 0, 4);"));
    card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + Theme.PANEL_COLOR1
        + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"));

    return card;
  }
}
