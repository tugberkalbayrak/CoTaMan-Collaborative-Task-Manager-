package com.example;

import java.util.List;

public class Group {
    private String groupId;
    private String groupName;
    private String courseCode;
    private List<User> members;
    private List<AcademicFile> groupArchive;

    public void addMember(User newMember) { /* ... */ }
    public List<CalendarEvent> getAggregateSchedule() { /* returns combined schedule of all members */ return null; }
}










