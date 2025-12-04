package com.example;

import java.util.List;

public class User {
    private String bilkentId;
    private String fullName;
    private String email;
    private List<CalendarEvent> schedule;
    private List<Group> enrolledGroups;
    private List<User> friends;

    public User(String id, String name) {}
    
    // Getters & Setters
    public List<CalendarEvent> getSchedule() { return schedule; }
    public void addEvent(CalendarEvent e) { }
}
