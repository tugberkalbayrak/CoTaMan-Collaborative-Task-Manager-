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
        // 1. Calculate Time
        LocalDateTime start = LocalDateTime.now().plusDays(dayIndex).withHour(8 + timeIndex);
        LocalDateTime end = start.plusHours(1);

        // 2. Create Application Model (for UI/Logic)
        CalendarEvent appEvent = new CalendarEvent(owner, title, location, start, end, Importance.MUST);
        
        // 3. Create Database Model (for Storage)
        CalendarEvent dbEvent = new CalendarEvent();
        dbEvent.setTitle(appEvent.getTitle());
        dbEvent.setLocation(appEvent.getLocation());
        dbEvent.setStartTime(appEvent.getStartTime());
        dbEvent.setEndTime(appEvent.getEndTime());
        // dbEvent.setImportance(appEvent.getImportance()); // Uncomment if DB Event has Enum
        dbEvent.setOwner(appEvent.getOwner()); // Should be current user ID
        
        // 4. Save
        repository.saveEvent(dbEvent);
        System.out.println("Event saved to DB: " + title);
    }
}