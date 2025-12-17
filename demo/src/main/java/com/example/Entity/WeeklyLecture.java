package com.example.Entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class WeeklyLecture {
    private String courseCode; // e.g., "MATH 102 - 001"
    private String room; // e.g., "B-103"
    private String type; // e.g., "Face-to-face Lecture"
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;

    public WeeklyLecture(String courseCode, String room, String type, DayOfWeek day, LocalTime startTime,
            LocalTime endTime) {
        this.courseCode = courseCode;
        this.room = room;
        this.type = type;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getRoom() {
        return room;
    }

    public String getType() {
        return type;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s (%s - %s)", day, courseCode, room, type, startTime, endTime);
    }
}
