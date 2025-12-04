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


    public boolean overlaps(CalendarEvent other) {  return false; }
    
    @Override
    public int compareTo(CalendarEvent other) { return 0; }
}
