package com.example.Entity;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class User {

    @BsonId
    private ObjectId id;

    @BsonProperty("fullName")
    private String fullName;

    @BsonProperty("email")
    private String email;

    @BsonProperty("password")
    private String password;

    @BsonProperty("bilkentId")
    private String bilkentId;

    @BsonProperty("schedule")
    private List<CalendarEvent> schedule = new ArrayList<>();

    @BsonProperty("enrolledGroups")
    private List<Group> enrolledGroups = new ArrayList<>();

    @BsonProperty("friends")
    private List<User> friends = new ArrayList<>();

    public User() {
    }

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // Getter ve Setterlar
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBilkentId() {
        return bilkentId;
    }

    public void setBilkentId(String bilkentId) {
        this.bilkentId = bilkentId;
    }

    public List<CalendarEvent> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<CalendarEvent> schedule) {
        this.schedule = schedule;
    }

    public void addEvent(CalendarEvent e) {
        if (schedule == null)
            schedule = new ArrayList<>();
        schedule.add(e);
    }

    public List<Group> getEnrolledGroups() {
        return enrolledGroups;
    }

    public void setEnrolledGroups(List<Group> enrolledGroups) {
        this.enrolledGroups = enrolledGroups;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}