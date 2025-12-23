package com.example.Handlers;

import com.example.Entity.CalendarEvent;
import com.example.Entity.Importance;
import com.example.Entity.User;
import com.example.database.CloudRepository;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class CalendarInteractionHandler {

    private CloudRepository repository;

    public CalendarInteractionHandler() {
        this.repository = new CloudRepository();
    }

    public void onAddEvent(User owner, String title, String location, int dayIndex, int timeIndex, String color) {
        LocalDateTime start = LocalDateTime.now().plusDays(dayIndex).withHour(8 + timeIndex);
        LocalDateTime end = start.plusHours(1);

        CalendarEvent appEvent = new CalendarEvent(owner, title, location, start, end, Importance.MUST);

        CalendarEvent dbEvent = new CalendarEvent();
        dbEvent.setTitle(appEvent.getTitle());
        dbEvent.setLocation(appEvent.getLocation());
        dbEvent.setStartTime(appEvent.getStartTime());
        dbEvent.setEndTime(appEvent.getEndTime());
        dbEvent.setOwner(appEvent.getOwner());
        repository.saveEvent(dbEvent);
        System.out.println("Event saved to DB: " + title);
    }
}