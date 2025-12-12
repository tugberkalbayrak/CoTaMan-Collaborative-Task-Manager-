package com.example;

import java.time.LocalDateTime;

public class CalendarEvent implements Comparable<CalendarEvent> {
    private String eventId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Importance importance;
    private String location;

    // Constructor
    public CalendarEvent(String title, LocalDateTime start, LocalDateTime end, Importance imp) {  }

    //Also default constructor.
    public CalendarEvent() {}


    public String getEventId() {
        return eventId;
    }


    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }


    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    public LocalDateTime getEndTime() {
        return endTime;
    }


    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    public Importance getImportance() {
        return importance;
    }


    public void setImportance(Importance importance) {
        this.importance = importance;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public boolean overlaps(CalendarEvent other) {  return false; }
    
    @Override
    public int compareTo(CalendarEvent other) { return 0; }
}
