package com.example.Entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;

public class CalendarEvent implements Comparable<CalendarEvent> {
    private ObjectId eventId;
    private User owner;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Importance importance;
    private String location;
    

    // Constructor
    public CalendarEvent(User owner, String title, String location,LocalDateTime start, LocalDateTime end, Importance imp) {
        this.owner = owner;
        this.title = title;
        this.location = location;
        this.startTime = start;
        this.endTime = end;
        this.importance = imp;
    }

    //Also default constructor.
    public CalendarEvent() {}


    public ObjectId getEventId() {
        return eventId;
    }


    public void setEventId(ObjectId eventId) {
        this.eventId = eventId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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


    public boolean overlaps(CalendarEvent other) {
        return (this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime));
    }
    
    @Override
    public int compareTo(CalendarEvent other) {
        // Sort by Importance (High to Low), then by Date
        int priorityComparison = Integer.compare(other.importance.getWeight(), this.importance.getWeight());
        if (priorityComparison != 0) return priorityComparison;
        return this.startTime.compareTo(other.startTime);
    }

    public Duration getDuration() {
        if (startTime != null && endTime != null) {
            return Duration.between(startTime, endTime);
        }
        return Duration.ZERO;
    }
}