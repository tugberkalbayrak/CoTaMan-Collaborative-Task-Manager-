package com.example;

import com.example.database.CloudRepository;

import java.time.LocalDateTime;

import com.example.Entity.CalendarEvent;
import com.example.database.Event;


public class CalendarInteractionHandler {

    private CloudRepository repository;

    public CalendarInteractionHandler() {
        this.repository = new CloudRepository();
    }

    public void onAddEvent(String title, int dayIndex, int timeIndex, String color) {
        // Convert UI indices to Actual DateTimes
        LocalDateTime start = LocalDateTime.now().plusDays(dayIndex).withHour(8 + timeIndex);
        LocalDateTime end = start.plusHours(1);

        // 1. Create the Application Model Object
        CalendarEvent calendarEvent = new CalendarEvent(title, start, end, Importance.MUST);
        calendarEvent.setLocation("Bilkent"); 
        
        // 2. Convert to Database Entity (Event)
        // Since direct casting (Event) calendarEvent fails, we must create a new Event object
        // and copy the fields.
        Event dbEvent = new Event();
        dbEvent.setTitle(calendarEvent.getTitle());
        dbEvent.setStartTime(calendarEvent.getStartTime());
        dbEvent.setEndTime(calendarEvent.getEndTime());
        // dbEvent.setImportance(calendarEvent.getImportance()); // If Event has this field
        
        // 3. Save to Repository
        repository.saveEvent(dbEvent); 
        System.out.println("Event saved to DB: " + title);
    }
}