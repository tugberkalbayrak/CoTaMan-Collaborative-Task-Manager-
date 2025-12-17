package com.example.database;

import org.bson.types.ObjectId;
import java.util.List;
import java.util.ArrayList;

public class Group {
    private ObjectId id;
    private String name;
    private String courseCode;
    private List<ObjectId> memberIds; 

    public Group() {}

    public Group(String name, String courseCode) {
        this.name = name;
        this.courseCode = courseCode;
        this.memberIds = new ArrayList<>();
    }

    // Getter - Setter
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ObjectId> getMemberIds() { return memberIds; }
    public void setMemberIds(List<ObjectId> memberIds) { this.memberIds = memberIds; }
}