package com.example.Entity;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import com.example.ui.components.Task; // Task sınıfını tanıtalım

public class Group {
    private ObjectId id;
    private String groupName;
    private String courseCode;
    private List<User> members;
    private List<ObjectId> memberIds;
    private List<AcademicFile> groupArchive;

    // --- YENİ EKLENECEK KISIM ---
    private List<Task> tasks;

    public Group() {
        this.memberIds = new ArrayList<>();
        this.groupArchive = new ArrayList<>();
        this.tasks = new ArrayList<>(); // Listeyi başlat
    }

    public Group(String name, String courseCode) {
        this.groupName = name;
        this.courseCode = courseCode;
        this.memberIds = new ArrayList<>();
        this.groupArchive = new ArrayList<>();
        this.tasks = new ArrayList<>(); // Listeyi başlat
    }

    // ... Diğer Getter/Setter'lar aynı kalsın ...

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public List<ObjectId> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<ObjectId> memberIds) {
        this.memberIds = memberIds;
    }

    public List<AcademicFile> getGroupArchive() {
        return groupArchive;
    }

    public void setGroupArchive(List<AcademicFile> groupArchive) {
        this.groupArchive = groupArchive;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    // --- YENİ GETTER & SETTER ---
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}