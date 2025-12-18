package com.example.Entity;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class Group {
    private ObjectId groupId;
    private String groupName;
    private String courseCode;
    private List<User> members;
    private List<AcademicFile> groupArchive;

    public Group() {}

    public Group(String name, String courseCode) {
        this.groupName = name;
        this.courseCode = courseCode;
        this.members = new ArrayList<>();
    }

    public void addMember(User newMember) { /* ... */ }
    public List<CalendarEvent> getAggregateSchedule() { /* returns combined schedule of all members */ return null; }

    public ObjectId getGroupId() {
        return groupId;
    }

    public void setGroupId(ObjectId groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<AcademicFile> getGroupArchive() {
        return groupArchive;
    }

    public void setGroupArchive(List<AcademicFile> groupArchive) {
        this.groupArchive = groupArchive;
    }
}