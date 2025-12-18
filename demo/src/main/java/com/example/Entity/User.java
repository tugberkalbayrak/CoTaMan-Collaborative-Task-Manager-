package com.example.Entity;

import java.util.List;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String bilkentId;
    private String fullName;
    private String email;
    private List<CalendarEvent> schedule;
    private List<Group> enrolledGroups;
    private List<User> friends;

    public User() {}

    public User(String id, String name, String email) {}
    // Getters & Setters
    public List<CalendarEvent> getSchedule() { return schedule; }
    public void addEvent(CalendarEvent e) { }


    public ObjectId getId() {
        return id;
    }

    public String getBilkentId() {
        return bilkentId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setBilkentId(String bilkentId) {
        this.bilkentId = bilkentId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return "User{name='" + fullName + "', email='" + email + "'}";
    }
}
